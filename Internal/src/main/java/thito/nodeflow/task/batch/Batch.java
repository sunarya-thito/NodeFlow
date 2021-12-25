package thito.nodeflow.task.batch;

import thito.nodeflow.task.TaskThread;

public class Batch {

    public static Task empty() {
        return new Batch().$empty();
    }

    public static Task execute(TaskThread taskThread, ProgressedTask task) {
        return new Batch().$execute(taskThread, task);
    }

    private Batch() {}

    Task root;
    int count;
    public Task $execute(TaskThread taskThread, ProgressedTask task) {
        Task t;
        if (root == null) {
            root = t = new Task(this, taskThread, task);
            count++;
        } else {
            root.push(t = new Task(this, taskThread, task));
        }
        return t;
    }

    public Task $empty() {
        return $execute(TaskThread.BG(), progress -> {});
    }

    void execute(Progress progress, Runnable onDone) {
        if (root == null) {
            if (onDone != null) {
                onDone.run();
            }
            return;
        }
        TaskProgress taskProgress = new TaskProgress(this, progress, root, onDone);
        taskProgress.execute();
    }

    public static class Task implements LazyProgressedTask {
        Batch batch;
        ProgressedTask progressedTask;
        TaskThread thread;
        Task next;
        Task(Batch batch, TaskThread thread, ProgressedTask progressedTask) {
            this.batch = batch;
            this.thread = thread;
            this.progressedTask = progressedTask;
        }

        @Override
        public void run(TaskProgress progress) throws Throwable {
            if (batch.root == null) {
                return;
            }
            batch.execute(progress.progress, progress::proceed);
        }

        Task cloneTasks(Batch newOwner) {
            Task cloned = cloneTask(newOwner);
            cloned.batch = newOwner;
            Task next = this.next;
            while (next != null) {
                Task childTask = next.cloneTask(newOwner);
                cloned.push(childTask);
                next = next.next;
            }
            return cloned;
        }

        public Task execute(Task task) {
            return execute(TaskThread.BG(), task);
        }

        public Task execute(TaskThread taskThread, ProgressedTask task) {
            Task next = new Task(batch, taskThread, task);
            push(next);
            return next;
        }

        public Task lazy(TaskThread taskThread, LazyProgressedTask task) {
            return execute(taskThread, task);
        }

        void insert(Task childTask) {
            if (childTask == null) return;
            if (childTask.batch != batch) throw new IllegalArgumentException("different context");
            batch.count += childTask.batch.count;
            childTask.push(next);
            next = childTask;
        }

        void push(Task childTask) {
            if (childTask == null) return;
            if (childTask.batch != batch) throw new IllegalArgumentException("different context");
            Task next = this;
            while (next.next != null) {
                next = next.next;
            }
            next.next = childTask;
            batch.count += childTask.batch.count;
        }

        Task cloneTask(Batch newOwner) {
            return new Task(newOwner, thread, progressedTask);
        }

        public void start() {
            start(Progress.create());
        }

        public void start(Progress progress) {
            batch.execute(progress, null);
        }

        public void start(TaskQueue taskQueue) {
            if (taskQueue != null) {
                taskQueue.executeBatch(this);
            } else {
                start(Progress.create());
            }
        }
    }
}
