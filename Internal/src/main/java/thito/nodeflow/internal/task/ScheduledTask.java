package thito.nodeflow.internal.task;

public interface ScheduledTask {
    void cancel();
    TaskState getState();
}
