package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

public class DynamicTaskImpl extends TaskImpl implements DynamicTask {

    private double progress;
    public DynamicTaskImpl(Task task) {
        super(task.getName(), (Runnable) task);
    }

    @Override
    public void run() {
        super.run();
        // fetch them inside the thread
        // do not fetch them outside of this
        // method because it might be called
        // outside of the thread
        progress = DynamicTask.PROGRESS.get().get();
    }

    @Override
    public double getProgress() {
        return progress;
    }

}
