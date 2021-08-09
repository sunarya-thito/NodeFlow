package thito.nodeflow.internal.task;

import javafx.util.*;

public interface TaskThread {
    ScheduledTask schedule(Runnable runnable);
    ScheduledTask schedule(Runnable runnable, Duration delay);
    ScheduledTask schedule(Runnable runnable, Duration delay, Duration period);
}
