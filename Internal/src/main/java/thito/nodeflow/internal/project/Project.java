package thito.nodeflow.internal.project;

import javafx.beans.property.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.config.*;
import thito.nodeflow.library.resource.*;

import java.util.*;

public class Project {
    private Resource directory;
    private Workspace workspace;
    private Resource sourceFolder;
    private ProjectProperties properties;
    private Section configuration;
    private ObjectProperty<Editor> editor = new SimpleObjectProperty<>();

    private List<ProjectExport.Handler> exportHandlerList = new ArrayList<>();

    public Project(Workspace workspace, ProjectProperties properties) {
        this.workspace = workspace;
        this.properties = properties;
        this.directory = properties.getDirectory();
        configuration = properties.getConfig().getMap("configuration").orElse(new MapSection());
        sourceFolder = properties.getDirectory().getChild("src");
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
