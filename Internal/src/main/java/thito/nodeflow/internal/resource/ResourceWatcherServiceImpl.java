package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class ResourceWatcherServiceImpl implements ResourceWatcherService {

    private WatchService watchService;
    private final Set<ResourceWatcher> watcherSet = ConcurrentHashMap.newKeySet();
    private final Thread thread = new Thread(this::start);
    private boolean active;

    public ResourceWatcherServiceImpl() {
        initializeService();
    }

    private void initializeService() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new ReportedError(e);
        }
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    private void start() {
        active = true;
        for (;;) {
            try {
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Object rawContext = event.context();
                    if (rawContext instanceof Path) {
                        Path filePath = (Path) rawContext;
                        File file = ((Path) key.watchable()).resolve(filePath).toFile();
                        ResourceWatcherEvent.Type type;
                        if (kind == ENTRY_CREATE) {
                            type = ResourceWatcherEvent.Type.CREATE;
                        } else if (kind == ENTRY_DELETE) {
                            type = ResourceWatcherEvent.Type.DELETE;
                        } else if (kind == ENTRY_MODIFY) {
                            type = ResourceWatcherEvent.Type.MODIFY;
                        } else {
                            continue;
                        }
                        Resource resource = ResourceManagerImpl.fileToResource(file);
                        ResourceWatcherEvent watcherEvent = new ResourceWatcherEventImpl(resource, type);
                        for (ResourceWatcher watcher : watcherSet) {
                            Resource watched = watcher.getWatchedResource();
                            if (watched.getPath().equals(key.watchable().toString()) && watcher instanceof ResourceWatcherImpl) {
                                ((ResourceWatcherImpl) watcher).dispatchEvent(watcherEvent);
                            }
                        }
                    }

                }
                key.reset();
                if (!active) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new ReportedError(e);
            }
        }
    }

    public void stop() {
        active = false;
    }

    @Override
    public ResourceWatcher createWatcher(ResourceDirectory resource) {
        ResourceWatcherImpl watcher = new ResourceWatcherImpl(resource, this);
        if (watcherSet.add(watcher) && resource instanceof PhysicalResource) {
            try {
                watcher.setRegisterKey(((PhysicalResource) resource).getSystemPath().register(watchService, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY));
            } catch (IOException e) {
                throw new ReportedError(e);
            }
        }
        return watcher;
    }

    @Override
    public Set<ResourceWatcher> getAllActiveWatchers() {
        return watcherSet;
    }

    @Override
    public void destroyWatcher(ResourceWatcher watcher) {
        if (watcherSet.remove(watcher) && watcher instanceof ResourceWatcherImpl) {
            WatchKey key = ((ResourceWatcherImpl) watcher).getRegisterKey();
            if (key != null) {
                key.cancel();
            }
        }
    }
}
