package thito.nodeflow.task;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.*;

public interface TaskThread {
    static TaskThread IO() {
        return TaskManager.getInstance().getIOThread();
    }
    static TaskThread UI() {
        return TaskManager.getInstance().getUIThread();
    }
    static TaskThread BG() {
        return TaskManager.getInstance().getBackgroundThread();
    }
    boolean isInThread();
    ScheduledTask schedule(Runnable runnable);
    ScheduledTask schedule(Runnable runnable, Duration delay);
    ScheduledTask schedule(Runnable runnable, Duration delay, Duration period);
    LongProperty timeInMillisProperty();
    void shutdown();
    String getThreadName();
    default void checkThread() {
        if (!isInThread()) throw new IllegalStateException("not in "+getThreadName()+" thread");
    }
    default <T extends Observable> T lock(T observable) {
        observable.addListener(obs -> checkThread());
        return observable;
    }
}
