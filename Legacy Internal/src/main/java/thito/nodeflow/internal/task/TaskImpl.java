package thito.nodeflow.internal.task;

public class TaskImpl extends AbstractTaskImpl {
    private final Runnable runnable;
    public TaskImpl(String name, Runnable runnable) {
        super(name);
        this.runnable = runnable;
    }

    @Override
    public void runTask() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
