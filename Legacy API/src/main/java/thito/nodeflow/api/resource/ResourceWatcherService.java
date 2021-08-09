package thito.nodeflow.api.resource;

import java.util.Set;

public interface ResourceWatcherService {
    ResourceWatcher createWatcher(ResourceDirectory resource);

    Set<ResourceWatcher> getAllActiveWatchers();

    void destroyWatcher(ResourceWatcher watcher);
}
