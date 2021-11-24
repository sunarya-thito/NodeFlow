package thito.nodeflow.resource;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import thito.nodeflow.annotation.IOThread;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.WatchKey;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Resource {
    protected LongProperty size = new SimpleLongProperty();
    protected ObjectProperty<ResourceType> type = new SimpleObjectProperty<>();
    protected ObservableList<Resource> children = FXCollections.observableArrayList();
    protected LongProperty modifiedDate = new SimpleLongProperty();
    protected File file;
    protected ResourceManager resourceManager;
    protected WatchKey watchKey;
    protected RandomAccessFile randomAccessFile;

    {
        type.addListener((obs, old, val) -> {
            if (val == ResourceType.DIRECTORY) {
                updateChildren();
                addToWatchList();
            } else {
                removeFromWatchList();
            }
        });
    }

    @IOThread
    public Resource(ResourceManager resourceManager, File file) {
        this.resourceManager = resourceManager;
        this.file = file;
        modifiedDate.addListener((obs, old, val) -> {
            if (updatingProperties) return;
            updatingProperties = true;
            if (file.setLastModified(val.longValue())) {
                modifiedDate.set(old.longValue());
            }
            updatingProperties = false;
        });
    }

    public Resource getParent() {
        return getResourceManager().getResource(toFile().getParentFile());
    }

    @IOThread
    protected void updateChildren() {
        String[] children = file.list();
        if (children != null) {
            for (String child : children) {
                Resource resource = resourceManager.getResource(new File(file, child));
                this.children.add(resource);
                resource.updateProperties();
            }
        }
    }

    private void removeFromWatchList() {
        if (watchKey != null) {
            watchKey.cancel();
        }
    }

    private void addToWatchList() {
        removeFromWatchList();
        watchKey = resourceManager.addWatchList(this);
    }

    public String getFileName() {
        return toFile().getName();
    }

    public String getPath() {
        return getPath(getResourceManager().getRoot());
    }

    public String getPath(Resource root) {
        StringBuilder builder = new StringBuilder();
        Resource resource = this;
        while (resource != null) {
            if (resource.equals(root)) {
                break;
            }
            if (builder.length() > 0) {
                builder.insert(0, File.separator);
            }
            builder.insert(0, resource.getFileName());
            resource = resource.getParent();
        }
        return builder.toString();
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

    public File toFile() {
        return file;
    }

    public boolean isSameFile(File file) {
        return toFile().getAbsolutePath().equals(file.getAbsolutePath());
    }

    public URL getURL() {
        try {
            return toFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean updatingProperties;

    @IOThread
    protected synchronized void updateLastModified() {
        if (updatingProperties) return;
        updatingProperties = true;
        File file = toFile();
        modifiedDate.set(file.lastModified());
        updatingProperties = false;
    }

    @IOThread
    protected synchronized void updateProperties() {
        try {
            File file = toFile();
            updateLastModified();
            if (file.exists() && file.isDirectory()) {
                type.set(ResourceType.DIRECTORY);
                size.set(ResourceManager.directorySize(toFile().toPath()));
            } else if (file.exists()) {
                type.set(ResourceType.FILE);
                size.set(file.length());
            } else {
                type.set(ResourceType.UNKNOWN);
                size.set(0);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @IOThread
    public boolean exists() {
        return typeProperty().get() != ResourceType.UNKNOWN;
    }

    @IOThread
    public BooleanBinding existsBinding() {
        return typeProperty().isNotEqualTo(ResourceType.UNKNOWN);
    }

    @IOThread
    public Resource getChild(String path) {
        return getResourceManager().getResource(new File(toFile(), path));
    }

    @IOThread
    public ObservableList<Resource> getChildren() {
        return FXCollections.unmodifiableObservableList(children);
    }

    @IOThread
    public RandomAccessFile openRandomAccess() throws IOException {
        if (randomAccessFile == null) randomAccessFile = new RandomAccessFile(toFile(), "rw");
        return randomAccessFile;
    }

    @IOThread
    public InputStream openInput() throws IOException {
        return new FileInputStream(toFile());
    }

    @IOThread
    public OutputStream openOutput() throws IOException {
        return new FileOutputStream(toFile());
    }

    @IOThread
    public Reader openReader() throws IOException {
        return new FileReader(toFile());
    }

    @IOThread
    public Writer openWriter() throws IOException {
        return new FileWriter(toFile());
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

    @IOThread
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
        if (!(o instanceof Resource resource)) return false;
        return Objects.equals(file.getAbsolutePath(), resource.file.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    public String toString() {
        return file.getAbsolutePath();
    }

}
