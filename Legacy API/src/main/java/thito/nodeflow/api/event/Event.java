package thito.nodeflow.api.event;

import thito.nodeflow.api.NodeFlow;

public interface Event {
    static <T extends Event> T call(T event) {
        NodeFlow.getApplication().getEventManager().callEvent(event);
        return event;
    }
    void consume();

    boolean isConsumed();
}
