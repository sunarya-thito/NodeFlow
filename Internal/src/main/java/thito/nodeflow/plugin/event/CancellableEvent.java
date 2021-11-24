package thito.nodeflow.plugin.event;

public interface CancellableEvent extends Event {
    boolean isCancelled();
    void setCancelled(boolean cancel);
}
