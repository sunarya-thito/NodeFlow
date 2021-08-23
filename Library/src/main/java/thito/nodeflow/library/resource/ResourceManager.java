package thito.nodeflow.library.resource;

import thito.nodeflow.library.application.*;
import thito.nodeflow.library.task.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ResourceManager {
    protected Resource root;
    protected Set<Resource> watchedDirectories = ConcurrentHashMap.newKeySet();
    public ResourceManager(File root) {
        this.root = new Resource(this, root);
        addWatchList(this.root);
        ResourceWatcher.activeResourceManagers.add(this);
    }

    protected void addWatchList(Resource resource) {
        if (resource == null) return;
        watchedDirectories.add(resource);
        try {
            Path path = Paths.get(resource.getURL().toURI());
            path.register(ResourceWatcher.getResourceWatcher().getWatchService(),
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public Resource getInternalResource(String path) {
        return new Resource(this, path, true);
    }

    public Resource getRoot() {
        return root;
    }

    public Resource toResource(File file) {
        return new Resource(this, file);
    }

    public Resource getResource(String path) {
        String[] paths = path.split(File.separator);
        Resource lookup = root;
        for (int i = 0; i < paths.length; i++) {
            if (lookup == null) return toResource(new File(root.toFile(), path));
            int finalI = i;
            lookup = lookup.children.stream().filter(res -> res.getFileName().equals(paths[finalI])).findAny()
                    .orElse(null);
        }
        return lookup;
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
