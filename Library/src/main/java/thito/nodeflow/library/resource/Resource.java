package thito.nodeflow.library.resource;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.task.*;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class Resource {
    protected LongProperty size = new SimpleLongProperty();
    protected ObjectProperty<ResourceType> type = new SimpleObjectProperty<>();
    protected ObservableList<Resource> children = FXCollections.observableArrayList();
    protected LongProperty modifiedDate = new SimpleLongProperty();
    protected File file;
    protected ResourceManager resourceManager;
    protected WatchKey watchKey;

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
    protected synchronized void updateLastModified() {
        if (updatingProperties) return;
        updatingProperties = true;
        File file = toFile();
        modifiedDate.set(file.lastModified());
        updatingProperties = false;
    }
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

    public boolean exists() {
        return typeProperty().get() != ResourceType.UNKNOWN;
    }

    public BooleanBinding existsBinding() {
        return typeProperty().isNotEqualTo(ResourceType.UNKNOWN);
    }

    public Resource getChild(String path) {
        return getResourceManager().getResource(new File(toFile(), path));
    }

    public ObservableList<Resource> getChildren() {
        return FXCollections.unmodifiableObservableList(children);
    }

    public InputStream openInput() throws IOException {
        checkThread();
        return new FileInputStream(toFile());
    }

    public OutputStream openOutput() throws IOException {
        checkThread();
        return new FileOutputStream(toFile());
    }

    public Reader openReader() throws IOException {
        checkThread();
        return new FileReader(toFile());
    }

    public Writer openWriter() throws IOException {
        checkThread();
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

    public void fireEvent(Event event) {
        checkThread();
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

    private void checkThread() {
        if (!TaskThread.IO().isInThread()) throw new IllegalStateException("not on IO thread");
    }
}
