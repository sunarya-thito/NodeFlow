package thito.nodeflow.internal.task;

import javafx.util.*;

public interface Task extends Runnable {
    default ScheduledTask schedule(TaskThread thread) {
        return thread.schedule(this);
    }

    default ScheduledTask schedule(TaskThread thread, Duration delay) {
        return thread.schedule(this, delay);
    }

    default ScheduledTask schedule(TaskThread thread, Duration delay, Duration period) {
        return thread.schedule(this, delay, period);
    }
}
