package thito.nodeflow.internal.project;

import thito.nodeflow.library.config.*;
import thito.nodeflow.library.resource.*;

import java.util.*;

public class Project {
    private Resource sourceFolder;
    private ProjectProperties properties;
    private Section configuration;

    private List<ProjectExport.Handler> exportHandlerList = new ArrayList<>();

    public Project(Workspace workspace, ProjectProperties properties) {
        this.properties = properties;
        configuration = properties.getConfig().getMap("configuration").orElseThrow();
        sourceFolder = properties.getDirectory().getChild("src");
    }

    public ProjectProperties getProperties() {
        return properties;
    }

}
