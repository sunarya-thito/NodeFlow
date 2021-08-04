package thito.nodeflow.api.resource;

public interface ResourceWatcherEvent {

    Resource getResource();

    Type getType();

    enum Type {
        CREATE, MODIFY, DELETE
    }
}
