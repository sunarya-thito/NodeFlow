package thito.nodeflow.api.node.eventbus;

public interface CompiledEvent {
    void call(Object...args);
    EventNode getNode();
    EventProvider getProvider();
}
