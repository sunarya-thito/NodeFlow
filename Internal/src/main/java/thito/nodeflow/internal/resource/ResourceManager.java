package thito.nodeflow.internal.resource;

import javafx.collections.*;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class ResourceManager {
    protected static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
    protected Resource root;
    protected final ObservableList<WeakReference<Resource>> watchedResources = FXCollections.observableArrayList();

    public ResourceManager(File root) {
        Objects.requireNonNull(root);
        this.root = new Resource(this, root);
        watchedResources.add(new WeakReference<>(this.root));
        this.root.updateProperties();
        ResourceWatcher.activeResourceManagers.add(this);
    }

    protected WatchKey addWatchList(Resource resource) {
        if (resource == null) return null;
        try {
            Path path = Paths.get(resource.getURL().toURI());
            return path.register(ResourceWatcher.getResourceWatcher().getWatchService(),
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource getRoot() {
        return root;
    }

    protected Resource toResource(File file) {
        synchronized (watchedResources) {
            Resource resource = new Resource(this, file);
            watchedResources.add(new WeakReference<>(resource));
            resource.updateProperties();
            return resource;
        }
    }

    public Resource getResource(File file) {
        if (file == null) return null;
        synchronized (watchedResources) {
            Iterator<WeakReference<Resource>> resourceIterator = watchedResources.iterator();
            while (resourceIterator.hasNext()) {
                WeakReference<Resource> resource = resourceIterator.next();
                Resource res = resource.get();
                if (res == null) {
                    resourceIterator.remove();
                } else {
                    if (res.toFile().equals(file)) {
                        return res;
                    }
                }
            }
            return toResource(file);
        }
    }

    protected static long directorySize(Path path) {
        final AtomicLong size = new AtomicLong(0);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return size.get();
    }
}
