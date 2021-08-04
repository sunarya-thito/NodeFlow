package thito.nodeflow.api.task;

import thito.nodeflow.api.NodeFlow;

public interface Duration {
    Duration INFINITE = millis(-1);

    static Duration millis(long millis) {
        return NodeFlow.getApplication().getTaskManager().duration(millis);
    }

    static Duration seconds(long seconds) {
        return millis(1000 * seconds);
    }

    static Duration minutes(long minutes) {
        return seconds(minutes * 60);
    }

    static Duration hours(long hours) {
        return minutes(hours * 60);
    }

    static Duration days(long days) {
        return hours(days * 24);
    }

    long getDays();

    long getHours();

    long getMinutes();

    long getSeconds();

    long getMillis();

    long asDays();

    long asHours();

    long asMinutes();

    long asSeconds();

    long asMillis();

    default boolean isInfinite() {
        return asMillis() < 0;
    }
}
