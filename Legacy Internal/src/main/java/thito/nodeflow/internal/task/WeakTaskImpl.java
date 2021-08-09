package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

import java.lang.ref.*;

public class WeakTaskImpl extends AbstractTaskImpl implements WeakTask {
    private final WeakReference<Task> taskWeakReference;

    public WeakTaskImpl(Task task) {
        super(task.getName());
        taskWeakReference = new WeakReference<>(task);
    }

    @Override
    public boolean canRun() {
        return taskWeakReference.get() != null;
    }

    @Override
    public void runTask() {
        Task task = taskWeakReference.get();
        if (task instanceof AbstractTaskImpl) {
            ((AbstractTaskImpl) task).run();
        }
    }
}
