package thito.nodeflow.internal.plugin.event;

import thito.nodeflow.internal.*;

import java.util.logging.*;

public abstract class EventListener {
    private Listener listener;
    private EventPriority priority;
    private boolean ignoreCancelled;

    public EventListener(Listener listener, EventPriority priority, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
    }

    public Listener getListener() {
        return listener;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    public void handleEvent(Event event) {
        boolean wasCancelled = false;
        if (event instanceof CancellableEvent) {
            wasCancelled = ((CancellableEvent) event).isCancelled();
            if (ignoreCancelled && wasCancelled) {
                return;
            }
        }
        handle(event);
        if (priority == EventPriority.MONITOR &&
                !wasCancelled &&
                event instanceof CancellableEvent &&
                ((CancellableEvent) event).isCancelled()) {
            NodeFlow.getLogger().log(Level.WARNING, this+" cancelled an Event on MONITOR priority!");
        }
    }

    protected abstract void handle(Event event);

    public String toString() {
        return String.valueOf(listener);
    }

}
