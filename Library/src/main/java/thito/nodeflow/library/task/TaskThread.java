package thito.nodeflow.library.task;

import javafx.util.*;

public interface TaskThread {
    static TaskThread IO() {
        return TaskManager.getInstance().getIOThread();
    }
    static TaskThread UI() {
        return TaskManager.getInstance().getUIThread();
    }
    static TaskThread BACKGROUND() {
        return TaskManager.getInstance().getBackgroundThread();
    }
    boolean isInThread();
    ScheduledTask schedule(Runnable runnable);
    ScheduledTask schedule(Runnable runnable, Duration delay);
    ScheduledTask schedule(Runnable runnable, Duration delay, Duration period);
    void shutdown();
}
