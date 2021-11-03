package thito.nodeflow.internal.project;

import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;
import thito.nodeflow.internal.plugin.ProjectHandler;
import thito.nodeflow.internal.plugin.ProjectHandlerRegistry;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.internal.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private Resource directory;
    private Workspace workspace;
    private Resource sourceFolder;
    private ProjectProperties properties;
    private Section configuration;

    private List<ProjectHandler> projectHandlers = new ArrayList<>();

    public Project(Workspace workspace, ProjectProperties properties) {
        this.workspace = workspace;
        this.properties = properties;
        this.directory = properties.getDirectory();
        configuration = properties.getConfiguration().getMap("configuration").orElse(new MapSection());
        sourceFolder = properties.getDirectory().getChild("src");
        Settings.loadProjectSettings(properties);
    }

    public <T extends ProjectHandler> T getProjectHandler(ProjectHandlerRegistry registry) {
        return (T) projectHandlers.stream().filter(x -> x.getRegistry() == registry).findAny().orElse(null);
    }

    public List<ProjectHandler> getProjectHandlers() {
        return projectHandlers;
    }

    public Section getConfiguration() {
        return configuration;
    }

    public Resource getDirectory() {
        return directory;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Resource getSourceFolder() {
        return sourceFolder;
    }

    public ProjectProperties getProperties() {
        return properties;
    }

}
