package thito.nodeflow.api.resource;

import java.util.function.Consumer;

public interface ResourceWatcher {
    Resource getWatchedResource();

    void addListener(Consumer<ResourceWatcherEvent> eventListener);

    void removeListener(Consumer<ResourceWatcherEvent> eventListener);

    ResourceWatcherService getWatcherService();
}
