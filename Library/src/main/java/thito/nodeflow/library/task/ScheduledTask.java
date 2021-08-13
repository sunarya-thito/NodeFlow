package thito.nodeflow.library.task;

public interface ScheduledTask {
    void cancel();
    TaskState getState();
}
