package thito.nodeflow.ui.docker;

import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectContext;

public interface DockerComponent {
    I18n displayName();
    boolean allowMultipleView();
    boolean isMenuAccessible();
    boolean isDefaultComponent();
    DockerPosition getDefaultPosition();
    DockNode createDockNode(ProjectContext projectContext, DockNodeState dockNodeState);
}
