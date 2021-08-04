package thito.nodeflow.internal.event;

import thito.nodeflow.api.event.*;

public abstract class AbstractEvent implements Event {
    private boolean consume;
    @Override
    public final void consume() {
        consume = true;
    }

    @Override
    public final boolean isConsumed() {
        return consume;
    }
}
