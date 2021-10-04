package thito.nodeflow.plugin.base.java;

import thito.nodeflow.config.Section;
import thito.nodeflow.internal.plugin.Plugin;
import thito.nodeflow.internal.plugin.ProjectHandler;
import thito.nodeflow.internal.plugin.ProjectHandlerRegistry;
import thito.nodeflow.internal.project.Project;
import thito.nodeflow.plugin.base.BluePrint;

public class JavaProjectHandlerRegistry implements ProjectHandlerRegistry {
    @Override
    public String getId() {
        return "java";
    }

    @Override
    public ProjectHandler loadHandler(Project project, Section configuration) {
        return new JavaProjectHandler(Plugin.getPlugin(BluePrint.class), this);
    }

    @Override
    public void saveHandler(ProjectHandler handler, Section configuration) {

    }
}
