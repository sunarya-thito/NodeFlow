package thito.nodeflow.internal;

import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.task.*;

public class SimpleTaskQueue {
    private Runnable next;
    private Task currentTask;
    private TaskThread thread;

    public SimpleTaskQueue(TaskThread thread) {
        this.thread = thread;
    }

    public void markReady() {
        currentTask = null;
        putQuery(next);
        next = null;
    }

    public synchronized void putQuery(Runnable supplier) {
        if (supplier == null) return;
        if (currentTask != null) {
            next = supplier;
        } else {
            currentTask = new TaskImpl("search", () -> {
                supplier.run();
            });
            thread.runTask(currentTask);
        }
    }

}
