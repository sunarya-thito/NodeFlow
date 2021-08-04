package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

public class ResourceWatcherEventImpl implements ResourceWatcherEvent {

    private final Resource resource;
    private final Type type;

    public ResourceWatcherEventImpl(Resource resource, Type type) {
        this.resource = resource;
        this.type = type;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Type getType() {
        return type;
    }
}
