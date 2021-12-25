package thito.nodeflow.project;

import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private Resource directory;
    private Workspace workspace;
    private Resource sourceFolder;
    private ProjectProperties properties;
    private Section configuration;

    @IOThread
    public Project(Workspace workspace, ProjectProperties properties) {
        this.workspace = workspace;
        this.properties = properties;
        this.directory = properties.getDirectory();
        configuration = properties.getConfiguration().getMap("configuration").orElse(new MapSection());
        sourceFolder = properties.getDirectory().getChild("src");
        Settings.loadProjectSettings(properties);
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
