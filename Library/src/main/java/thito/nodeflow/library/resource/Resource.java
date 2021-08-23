package thito.nodeflow.library.resource;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Resource {
    protected LongProperty size = new SimpleLongProperty();
    protected ObjectProperty<ResourceType> type = new SimpleObjectProperty<>();
    protected ObservableList<Resource> children = FXCollections.observableArrayList();
    protected LongProperty modifiedDate = new SimpleLongProperty();
    protected URL url;
    protected ResourceManager resourceManager;

    public Resource(ResourceManager resourceManager, String path, boolean internal) {
        this.resourceManager = resourceManager;
        try {
            if (internal) {
                url = Resource.class.getClassLoader().getResource(path);
            } else {
                File file = new File(path);
                url = file.toURI().toURL();
                modifiedDate.addListener((obs, old, val) -> {
                    if (updatingProperties) return;
                    updatingProperties = true;
                    if (file.setLastModified(val.longValue())) {
                        modifiedDate.set(old.longValue());
                    }
                    updatingProperties = false;
                });
            }
            updateProperties();
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

    public Resource(ResourceManager resourceManager, File file) {
        this.resourceManager = resourceManager;
        try {
            url = file.toURI().toURL();
            modifiedDate.addListener((obs, old, val) -> {
                if (updatingProperties) return;
                updatingProperties = true;
                if (file.setLastModified(val.longValue())) {
                    modifiedDate.set(old.longValue());
                }
                updatingProperties = false;
            });
            updateProperties();
            scanChildren();
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

    public String getFileName() {
        return toFile().getName();
    }

    public String getName() {
        String name = getFileName();
        int index = name.lastIndexOf('.');
        if (index >= 0) name = name.substring(0, index);
        return name;
    }

    public String getExtension() {
        String name = getFileName();
        int index = name.lastIndexOf('.');
        if (index >= 0) return name.substring(index + 1);
        return null;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    protected void scanChildren() {
        File file = toFile();
        File[] list = file.listFiles();
        if (list != null) {
            for (File f : list) {
                Resource resource = new Resource(getResourceManager(), f);
                children.add(resource);
                if (resource.getType() == ResourceType.DIRECTORY) {
                    getResourceManager().addWatchList(resource);
                }
            }
        }
    }

    public File toFile() {
        return new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
    }

    public boolean isSameFile(File file) {
        return toFile().equals(file);
    }

    public URL getURL() {
        return url;
    }

    protected boolean updatingProperties;
    public void updateProperties() {
        if (updatingProperties) return;
        updatingProperties = true;
        try {
            URLConnection connection = url.openConnection();
            connection.setAllowUserInteraction(true);
            connection.connect();
            modifiedDate.set(connection.getLastModified());
            File file = toFile();
            if (file.isDirectory()) {
                type.set(ResourceType.DIRECTORY);
                size.set(ResourceManager.directorySize(Paths.get(url.toURI())));
            } else if (file.isFile()) {
                type.set(ResourceType.FILE);
                size.set(connection.getContentLengthLong());
            } else {
                type.set(ResourceType.UNKNOWN);
                size.set(0);
            }
            connection.getInputStream().close();
        } catch (FileNotFoundException e) {
            type.set(ResourceType.UNKNOWN);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        updatingProperties = false;
    }

    public boolean exists() {
        return typeProperty().get() != ResourceType.UNKNOWN;
    }

    public BooleanBinding existsBinding() {
        return typeProperty().isNotEqualTo(ResourceType.UNKNOWN);
    }

    public Resource getChild(String path) {
        String[] paths = path.split("/");
        Resource current = this;
        for (int i = 0; i < paths.length; i++) {
            if (current == null) return resourceManager.toResource(new File(toFile(), path));
            int finalI = i;
            current = current.children.stream().filter(x -> x.getFileName().equals(paths[finalI])).findAny().orElse(null);
        }
        return current;
    }

    public ObservableList<Resource> getChildren() {
        return FXCollections.unmodifiableObservableList(children);
    }

    public InputStream openInput() throws IOException {
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    public OutputStream openOutput() throws IOException {
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        return connection.getOutputStream();
    }

    public Reader openReader() throws IOException {
        return new InputStreamReader(openInput());
    }

    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutput());
    }

    private Map<EventType<?>, LinkedList<EventHandler<?>>> listener = new ConcurrentHashMap<>();

    public long getSize() {
        return size.get();
    }

    public long getModifiedDate() {
        return modifiedDate.get();
    }

    public ReadOnlyLongProperty sizeProperty() {
        return size;
    }

    public ReadOnlyObjectProperty<ResourceType> typeProperty() {
        return type;
    }

    public LongProperty modifiedDateProperty() {
        return modifiedDate;
    }

    public ResourceType getType() {
        return type.get();
    }

    public void fireEvent(Event event) {
        LinkedList<?> list = listener.get(event.getEventType());
        if (list != null) {
            for (Object o : list) {
                ((EventHandler<Event>) o).handle(event);
            }
        }
    }

    public <T extends Event> void addEventHandler(EventType<T> type, EventHandler<T> handler) {
        listener.computeIfAbsent(type, x -> new LinkedList<>()).add(handler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> type, EventHandler<T> handler) {
        LinkedList<?> list = listener.get(type);
        if (list != null) {
            list.remove(handler);
            if (list.isEmpty()) {
                listener.remove(list);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return url.equals(resource.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    public String toString() {
        return url.toExternalForm();
    }

}
