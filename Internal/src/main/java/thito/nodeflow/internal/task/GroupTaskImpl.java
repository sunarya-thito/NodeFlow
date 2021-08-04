package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

import java.util.*;

public class GroupTaskImpl extends AbstractTaskImpl implements GroupTask {

    private final List<? extends Task> tasks;
    private double progress;
    public GroupTaskImpl(String name, List<? extends Task> tasks) {
        super(name);
        this.tasks = tasks;
    }

    @Override
    public void runTask() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).run();
            }
            progress = i / (double) tasks.size();
        }
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public List<? extends Task> getTasks() {
        return tasks;
    }
}
