package thito.nodeflow.internal.plugin.event.project;

import thito.nodeflow.internal.plugin.event.*;
import thito.nodeflow.internal.project.*;

public class ProjectLoadEvent implements CancellableEvent {
    private boolean cancelled;
    private Project project;

    public ProjectLoadEvent(Project project) {
        this.project = project;
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
