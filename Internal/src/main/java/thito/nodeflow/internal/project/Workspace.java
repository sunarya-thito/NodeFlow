package thito.nodeflow.internal.project;

import javafx.collections.*;
import thito.nodeflow.config.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.annotation.IOThread;
import thito.nodeflow.internal.resource.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Workspace {
    private ResourceManager resourceManager;
    private ObservableList<ProjectProperties> projectPropertiesList = FXCollections.observableArrayList();

    @IOThread
    public Workspace(File root) {
        resourceManager = new ResourceManager(root);
    }

    public Resource getRoot() {
        return resourceManager.getRoot();
    }

    @IOThread
    public void scanProjects() {
        NodeFlow.getLogger().log(Level.INFO, "Scanning workspace "+resourceManager.getRoot());
        Resource resource = resourceManager.getRoot();
        List<ProjectProperties> added = new ArrayList<>();
        for (Resource res : resource.getChildren()) {
            Resource child = res.getChild("project.yml");
            if (child.getType() == ResourceType.FILE) {
                ProjectProperties existing = projectPropertiesList.stream().filter(x -> x.getDirectory().equals(res)).findAny().orElse(null);
                if (existing != null) {
                    added.add(existing);
                    continue;
                }
                try (Reader reader = child.openReader()) {
                    Section section = Section.parseToMap(reader);
                    ProjectProperties prop = new ProjectProperties(this, res, section);
                    added.add(prop);
                    projectPropertiesList.add(prop);
                    NodeFlow.getLogger().log(Level.INFO, "Project scanned "+prop.getName()+"!");
                } catch (Exception e) {
                    NodeFlow.getLogger().log(Level.WARNING, "failed to read project properties of "+res.getFileName(), e);
                }
            } else {
                NodeFlow.getLogger().log(Level.INFO, "Unable to load directory "+res+" as project, no "+child+" to be found! "+child.getType());
            }
        }
        projectPropertiesList.retainAll(added);
    }

    @IOThread
    public ProjectProperties createProject(String name, String directoryName) throws IOException {
        Resource directory = resourceManager.getRoot().getChild(directoryName);
        directory.toFile().mkdirs();
        Resource properties = directory.getChild("project.yml");
        Section section = new MapSection();
        section.set("name", name);
        section.set("last-modified", System.currentTimeMillis());
        try (Writer writer = properties.openWriter()) {
            writer.write(Section.toString(section));
        }
        ProjectProperties projectProperties = new ProjectProperties(this, directory, section);
        projectPropertiesList.add(projectProperties);
        return projectProperties;
    }

    public ObservableList<ProjectProperties> getProjectPropertiesList() {
        return projectPropertiesList;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void closeWorkspace() {

    }

}
