package thito.nodeflow.task;

public interface ScheduledTask {
    void cancel();
    TaskState getState();
}
