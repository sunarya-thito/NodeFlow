package thito.nodeflow.api.event;

public interface EventManager {
    <T extends Event> T callEvent(T event);

    void registerWeakListener(Listener listener);

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);
}
