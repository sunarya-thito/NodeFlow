package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class ResourceWatcherImpl implements ResourceWatcher {
    private final Set<Consumer<ResourceWatcherEvent>> listenerSet = ConcurrentHashMap.newKeySet();
    private final Resource resource;
    private final ResourceWatcherService watcherService;
    private WatchKey registerKey;

    public ResourceWatcherImpl(Resource resource, ResourceWatcherService watcherService) {
        this.resource = resource;
        this.watcherService = watcherService;
    }

    public void setRegisterKey(WatchKey registerKey) {
        this.registerKey = registerKey;
    }

    public WatchKey getRegisterKey() {
        return registerKey;
    }

    @Override
    public Resource getWatchedResource() {
        return resource;
    }

    public void dispatchEvent(ResourceWatcherEvent event) {
        listenerSet.forEach(x -> x.accept(event));
    }

    @Override
    public void addListener(Consumer<ResourceWatcherEvent> eventListener) {
        listenerSet.add(eventListener);
    }

    @Override
    public void removeListener(Consumer<ResourceWatcherEvent> eventListener) {
        listenerSet.remove(eventListener);
    }

    @Override
    public ResourceWatcherService getWatcherService() {
        return watcherService;
    }
}
