package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

public class DurationImpl implements Duration {
    private final long millis;

    public DurationImpl(long millis) {
        this.millis = millis;
    }

    @Override
    public long getDays() {
        return asDays();
    }

    @Override
    public long getHours() {
        return asHours() % 24;
    }

    @Override
    public long getMinutes() {
        return asMinutes() % 60;
    }

    @Override
    public long getSeconds() {
        return asSeconds() % 60;
    }

    @Override
    public long getMillis() {
        return asMillis() % 1000;
    }

    @Override
    public long asDays() {
        return asHours() / 24;
    }

    @Override
    public long asHours() {
        return asMinutes() / 60;
    }

    @Override
    public long asMinutes() {
        return asSeconds() / 60;
    }

    @Override
    public long asSeconds() {
        return millis / 1000L;
    }

    @Override
    public long asMillis() {
        return millis;
    }
}
