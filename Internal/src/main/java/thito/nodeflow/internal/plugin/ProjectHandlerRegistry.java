package thito.nodeflow.internal.plugin;

import thito.nodeflow.config.Section;
import thito.nodeflow.internal.project.Project;

public interface ProjectHandlerRegistry {
    String getId();
    ProjectHandler loadHandler(Project project, Section configuration);
    void saveHandler(ProjectHandler handler, Section configuration);
}
