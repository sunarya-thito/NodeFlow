package thito.nodeflow.plugin.event.project;

import thito.nodeflow.plugin.event.CancellableEvent;
import thito.nodeflow.project.Project;
import thito.nodeflow.task.batch.Batch;

public class ProjectUnloadEvent implements CancellableEvent {
    private boolean cancelled;
    private Project project;
    private Batch batch;

    public ProjectUnloadEvent(Project project, Batch batch) {
        this.project = project;
        this.batch = batch;
    }

    public Project getProject() {
        return project;
    }

    public Batch getBatchTask() {
        return batch;
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
