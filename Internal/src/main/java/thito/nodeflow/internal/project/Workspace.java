package thito.nodeflow.internal.project;

import javafx.collections.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.config.*;
import thito.nodeflow.library.resource.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Workspace {
    private ResourceManager resourceManager;
    private ObservableList<ProjectProperties> projectPropertiesList = FXCollections.observableArrayList();

    public Workspace(File root) {
        resourceManager = new ResourceManager(root);
    }

    public void scanProjects() {
        Resource resource = resourceManager.getRoot();
        List<ProjectProperties> added = new ArrayList<>();
        for (Resource res : resource.getChildren()) {
            if (ProjectProperties.isProjectFolder(res)) {
                try {
                    if (projectPropertiesList.stream().anyMatch(x -> x.getDirectory().equals(res))) continue;
                    ProjectProperties prop = new ProjectProperties(res);
                    added.add(prop);
                    projectPropertiesList.add(prop);
                } catch (Exception e) {
                    NodeFlow.getLogger().log(Level.WARNING, "failed to read project properties of "+res.getFileName());
                }
            }
        }
        projectPropertiesList.retainAll(added);
    }

    public ProjectProperties createProject(String name) throws IOException {
        Resource directory = resourceManager.getRoot().getChild(name);
        directory.toFile().mkdirs();
        Resource properties = directory.getChild("project.yml");
        try (Writer writer = properties.openWriter()) {
            Section section = new MapSection();
            section.set("name", name);
            writer.write(Section.toString(section));
        }
        ProjectProperties projectProperties = new ProjectProperties(directory);
        projectPropertiesList.add(projectProperties);
        return projectProperties;
    }

    public ObservableList<ProjectProperties> getProjectPropertiesList() {
        return projectPropertiesList;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
