package thito.nodeflow.library.resource;

import thito.nodeflow.library.task.*;

import java.io.*;
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
    private boolean running;

    public ResourceWatcher() {
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        thread = new Thread(this::startWatcher);
    }

    public void open() {
        if (running) return;
        running = true;
        thread.start();
    }

    public void close() {
        if (!running) return;
        running = false;
        thread.interrupt();
    }

    public WatchService getWatchService() {
        return service;
    }

    private void startWatcher() {
        while (running) {
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
                path = ((Path) watchable).resolve(path);
                File targetFile = path.toFile();
//                System.out.println("target: "+path+" from "+targetFile+" is "+kind+" list "+activeResourceManagers);
                for (ResourceManager resourceManager : activeResourceManagers) {
                    Iterator<Resource> iterator = resourceManager.watchedDirectories.iterator();
                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        if (resource == null) {
                            iterator.remove();
                            continue;
                        }
                        if (resource.isSameFile(currentFile)) {
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                Resource res = new Resource(resourceManager, targetFile);
                                TaskThread.IO().schedule(() -> {
                                    resource.children.add(res);
                                    if (res.getType() == ResourceType.DIRECTORY) {
                                        resourceManager.addWatchList(res);
                                    }
                                });
                            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                TaskThread.IO().schedule(() -> {
                                    resource.children.removeIf(res -> res.isSameFile(targetFile));
                                });
                            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                for (Resource res : resource.children) {
                                    if (res.isSameFile(targetFile)) {
                                        TaskThread.IO().schedule(() -> {
                                            res.updateProperties();
                                            res.fireEvent(new ResourceEvent(ResourceEvent.FILE_MODIFIED, res));
                                        });
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!key.reset()) {
                key.cancel();
            }
        }
    }
}
