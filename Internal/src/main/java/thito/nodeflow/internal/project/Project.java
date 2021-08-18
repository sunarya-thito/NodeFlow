package thito.nodeflow.internal.project;

import thito.nodeflow.library.resource.*;

import java.util.*;

public class Project {
    private ResourceManager resourceManager;
    private Resource sourceFolder;
    private Resource dependenciesFolder;

    private List<ProjectExport.Handler> exportHandlerList = new ArrayList<>();

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

}
