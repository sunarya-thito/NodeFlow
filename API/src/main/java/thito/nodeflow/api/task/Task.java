package thito.nodeflow.api.task;

import thito.nodeflow.api.NodeFlow;

public interface Task {
    static Task createTask(String name, Runnable runnable, TaskThread thread) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        task.setupThread(thread);
        return task;
    }
    static Task runOnBackground(String name, Runnable runnable) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getBackgroundThread().runTask(task);
        return task;
    }

    static Task runOnForeground(String name, Runnable runnable) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getForegroundThread().runTask(task);
        return task;
    }

    static Task runOnBackgroundLater(String name, Runnable runnable, Duration delay) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getBackgroundThread().runTaskLater(task, delay);
        return task;
    }

    static Task runOnForegroundLater(String name, Runnable runnable, Duration delay) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getForegroundThread().runTaskLater(task, delay);
        return task;
    }

    static Task runOnBackgroundRepeatedly(String name, Runnable runnable, Duration delay, Duration period) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getBackgroundThread().runTaskRepeatedly(task, delay, period);
        return task;
    }

    static Task runOnForegroundRepeatedly(String name, Runnable runnable, Duration delay, Duration period) {
        TaskManager manager = NodeFlow.getApplication().getTaskManager();
        Task task = manager.createTask(name, runnable);
        manager.getForegroundThread().runTaskRepeatedly(task, delay, period);
        return task;
    }

    void setupThread(TaskThread thread);

    TaskThread getThread();

    String getName();

    TaskState getState();

    Duration getAverageRunDuration();

    boolean isCancelled();

    void cancel();

    default void execute() {
        getThread().runTask(this);
    }

    default void executeRepeatedly(Duration delay, Duration period) {
        getThread().runTaskRepeatedly(this, delay, period);
    }

    default void executeLater(Duration delay) {
        getThread().runTaskLater(this, delay);
    }
}
