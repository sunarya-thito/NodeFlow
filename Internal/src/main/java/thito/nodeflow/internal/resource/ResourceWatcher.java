package thito.nodeflow.internal.resource;

import thito.nodeflow.internal.task.*;

import java.io.*;
import java.lang.ref.*;
import java.nio.file.*;
import java.util.*;

public class ResourceWatcher {
    static ResourceWatcher resourceWatcher = new ResourceWatcher();
    static Set<ResourceManager> activeResourceManagers = Collections.newSetFromMap(new WeakHashMap<>());

    public static ResourceWatcher getResourceWatcher() {
        return resourceWatcher;
    }

    private Thread thread;
    private WatchService service;

    public ResourceWatcher() {
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        thread = new Thread(this::startWatcher);
    }

    public void open() {
        if (thread.isAlive()) return;
        thread.start();
    }

    public void close() {
        if (!thread.isAlive()) return;
        thread.interrupt();
    }

    public WatchService getWatchService() {
        return service;
    }

    private void startWatcher() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = service.take();
            } catch (InterruptedException e) {
                return;
            }
            Watchable watchable = key.watchable();
            File currentFile = ((Path) watchable).toFile();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                Path path = (Path) event.context();
                File targetFile = path.toFile();
                for (ResourceManager resourceManager : activeResourceManagers) {
                    synchronized (resourceManager.watchedResources) {
                        Iterator<WeakReference<Resource>> iterator = resourceManager.watchedResources.iterator();
                        while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                            WeakReference<Resource> weakReference = iterator.next();
                            Resource resource = weakReference.get();
                            if (resource == null) {
                                iterator.remove();
                                continue;
                            }
                            if (resource.isSameFile(currentFile)) {
                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    TaskThread.IO().schedule(() -> {
                                        Resource target = resource.getChild(targetFile.getPath());
                                        resource.children.add(target);
                                        resource.updateProperties();
                                        target.updateProperties();
                                    });
                                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                    TaskThread.IO().schedule(() -> {
                                        Resource target = resource.getChild(targetFile.getPath());
                                        resource.children.remove(target);
                                        resource.updateProperties();
                                        target.updateProperties();
                                    });
                                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    TaskThread.IO().schedule(() -> {
                                        Resource target = resource.getChild(targetFile.getPath());
                                        target.updateProperties();
                                        target.fireEvent(new ResourceEvent(ResourceEvent.FILE_MODIFIED, target));
                                        resource.updateProperties();
                                        target.updateProperties();
                                    });
                                }
                            }
                        }
                    }
                }
            }

            if (!key.reset() || Thread.currentThread().isInterrupted()) {
                key.cancel();
            }
        }
    }
}
