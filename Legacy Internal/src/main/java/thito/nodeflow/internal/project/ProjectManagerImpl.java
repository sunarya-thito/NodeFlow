package thito.nodeflow.internal.project;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.*;

import java.util.*;

public class ProjectManagerImpl implements ProjectManager {

    private final List<Project> loadedProjects = new ArrayList<>();
    private final List<ProjectFacet> registeredFacets = new ArrayList<>();

    @Override
    public Project[] getLoadedProjects() {
        return loadedProjects.toArray(new Project[0]);
    }

    public List<ProjectFacet> getRegisteredFacets() {
        return registeredFacets;
    }

    @Override
    public ProjectFacet[] getFacets() {
        return registeredFacets.toArray(new ProjectFacet[0]);
    }

    @Override
    public void registerFacet(ProjectFacet facet) {
        if (!(getFacet(facet.getId()) instanceof UnknownFacet)) throw new IllegalArgumentException("facet with same the id is already registered: "+facet.getId());
        registeredFacets.add(facet);
        Toolkit.info("Facet registered: "+facet.getId()+" ("+facet.getName()+")");
    }

    @Override
    public void unregisterFacet(ProjectFacet facet) {
        registeredFacets.remove(facet);
    }

    @Override
    public ProjectFacet getFacet(String id) {
        for (int i = 0; i < registeredFacets.size(); i++) {
            ProjectFacet facet = registeredFacets.get(i);
            if (facet.getId().equals(id)) {
                return facet;
            }
        }
        return new UnknownFacet(id);
    }

    @Override
    public void storeProjectProperties(ProjectProperties projectProperties, WritableResourceFile propertiesFile) {
        Section section = Section.newMap();
        section.set(projectProperties.getName(), "name");
        section.set(projectProperties.getAuthor(), "author");
        ProjectFacet facet = projectProperties.getFacet();
        if (facet != null) {
            section.set(facet.getId(), "facet");
        }
        Section.saveYaml(section, propertiesFile);
    }

    @Override
    public ProjectProperties loadProjectProperties(ResourceFile propertiesFile) {
        Section section = Section.loadYaml(propertiesFile);
        return new ProjectPropertiesImpl(
                section.getString("name"),
                section.getString("author"),
                propertiesFile.getParentDirectory(),
                getFacet(section.getString("facet")),
                section.getLong("last-modified"));
    }

    @Override
    public Project loadProject(ProjectProperties projectProperties) {
        Project project = new ProjectImpl(projectProperties);
        loadedProjects.add(project);
//        UIManagerImpl.getInstance().getWindowsManager().getLauncher().getProjectsPage().refreshProjects();
        return project;
    }

    @Override
    public void unloadProject(Project project) {
        Toolkit.info("unloading project "+project.getProperties().getName());
        loadedProjects.remove(project);
        ((ProjectImpl) project).close();
//        UIManagerImpl.getInstance().getWindowsManager().getLauncher().getProjectsPage().refreshProjects();
    }
}
