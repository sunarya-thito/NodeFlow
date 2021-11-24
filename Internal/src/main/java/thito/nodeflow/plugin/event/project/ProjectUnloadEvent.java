package thito.nodeflow.plugin.event.project;

import thito.nodeflow.plugin.event.CancellableEvent;
import thito.nodeflow.project.Project;
import thito.nodeflow.task.BatchTask;

public class ProjectUnloadEvent implements CancellableEvent {
    private boolean cancelled;
    private Project project;
    private BatchTask batchTask;

    public ProjectUnloadEvent(Project project, BatchTask batchTask) {
        this.project = project;
        this.batchTask = batchTask;
    }

    public Project getProject() {
        return project;
    }

    public BatchTask getBatchTask() {
        return batchTask;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
