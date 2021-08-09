package thito.nodeflow.internal;

import javafx.beans.property.*;
import thito.nodeflow.api.task.*;

public class Statistic implements Runnable {

    public static final Statistic STATS = new Statistic();

    private LongProperty usedMemory = new SimpleLongProperty();
    private LongProperty totalMemory = new SimpleLongProperty();

    public Statistic() {
        Task.runOnForegroundRepeatedly("statistic", this, Duration.millis(1000), Duration.millis(1000));
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        totalMemory.set(runtime.totalMemory());
        usedMemory.set(runtime.totalMemory() - runtime.freeMemory());
    }

    public LongProperty usedMemoryProperty() {
        return usedMemory;
    }

    public LongProperty totalMemoryProperty() {
        return totalMemory;
    }
}
