package thito.nodeflow.internal.plugin.event;

public interface CancellableEvent extends Event {
    boolean isCancelled();
    void setCancelled(boolean cancel);
}
