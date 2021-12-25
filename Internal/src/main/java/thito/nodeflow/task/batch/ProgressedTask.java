package thito.nodeflow.task.batch;

public interface ProgressedTask {
    void run(TaskProgress progress) throws Throwable;
}
