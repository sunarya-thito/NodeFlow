package thito.nodeflow.plugin.event.project;

import thito.nodeflow.plugin.event.*;
import thito.nodeflow.project.Project;
import thito.nodeflow.task.batch.Batch;

public class ProjectLoadEvent implements CancellableEvent {
    private boolean cancelled;
    private Project project;
    private Batch batchTask;

    public ProjectLoadEvent(Project project, Batch batchTask) {
        this.project = project;
        this.batchTask = batchTask;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
