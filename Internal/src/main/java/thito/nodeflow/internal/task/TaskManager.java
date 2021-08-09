package thito.nodeflow.internal.task;

public class TaskManager {
    private TaskThread IOThread;
    private TaskThread backgroundThread;
    private TaskThread UIThread;

    public TaskThread getBackgroundThread() {
        return backgroundThread;
    }

    public TaskThread getIOThread() {
        return IOThread;
    }

    public TaskThread getUIThread() {
        return UIThread;
    }
}
