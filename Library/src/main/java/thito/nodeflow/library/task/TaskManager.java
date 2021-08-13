package thito.nodeflow.library.task;

import java.util.logging.*;

public class TaskManager {
    static Logger logger = Logger.getLogger(TaskManager.class.getName());
    private static TaskManager instance;

    public static Logger getLogger() {
        return logger;
    }

    public static TaskManager getInstance() {
        return instance;
    }

    private TaskThread IOThread;
    private TaskThread backgroundThread;
    private TaskThread UIThread;

    public TaskManager() {
        if (instance != null) throw new IllegalStateException("already initialized");
        instance = this;
    }

    public void shutdown() {
        instance = null;
        IOThread.shutdown();
        backgroundThread.shutdown();
        UIThread.shutdown();
    }

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
