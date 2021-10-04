package thito.nodeflow.internal.project;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import thito.nodeflow.config.*;
import thito.nodeflow.internal.plugin.PluginManager;
import thito.nodeflow.internal.plugin.ProjectHandler;
import thito.nodeflow.internal.plugin.ProjectHandlerRegistry;
import thito.nodeflow.internal.project.module.FileModule;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.internal.resource.*;

import java.util.*;

public class Project {
    private Resource directory;
    private Workspace workspace;
    private Resource sourceFolder;
    private ProjectProperties properties;
    private Section configuration;
    private ObjectProperty<Editor> editor = new SimpleObjectProperty<>();

    private List<ProjectHandler> projectHandlers = new ArrayList<>();
    private List<ProjectExport.Handler> exportHandlerList = new ArrayList<>();

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

    public ObjectProperty<Editor> editorProperty() {
        return editor;
    }

    public ProjectProperties getProperties() {
        return properties;
    }

}
