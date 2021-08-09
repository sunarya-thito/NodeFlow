package thito.nodeflow.internal.project;

import javafx.beans.property.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.resource.*;

import java.io.*;
import java.util.*;

public class ProjectPropertiesImpl implements ProjectProperties {
    private final ResourceDirectory workingDirectory;
    private ProjectFacet facet;
    private long lastModified;
    private StringProperty name = new SimpleStringProperty();
    private StringProperty author = new SimpleStringProperty();

    private static String dummyName() {
        Random random = new Random();
        int total = random.nextInt(10);
        StringBuilder builder = new StringBuilder(total + 3);
        for (int i = 0; i < total + 3; i++) {
            builder.append((char) ('a' + random.nextInt(26)));
        }
        return builder.toString();
    }
    private static long counter = 0;
    public static ProjectProperties createDummy() {
        return new ProjectPropertiesImpl(dummyName()+" #"+(++counter), dummyName(), new FileResourceDirectoryImpl(new File("")), null, System.currentTimeMillis());
    }

    public ProjectPropertiesImpl(String name, String author, ResourceDirectory workingDirectory, ProjectFacet facet, long lastModified) {
        this.name.set(name);
        this.workingDirectory = workingDirectory;
        this.facet = facet;
        this.lastModified = lastModified;
        this.author.set(author);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPropertiesImpl that = (ProjectPropertiesImpl) o;
        return lastModified == that.lastModified && getName().equals(that.getName()) && workingDirectory.equals(that.workingDirectory) && Objects.equals(facet, that.facet) && getAuthor().equals(that.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, workingDirectory, facet, lastModified, author);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ProjectFacet getFacet() {
        return facet;
    }

    @Override
    public void setFacet(ProjectFacet facet) {
        this.facet = facet;
    }

    @Override
    public void rename(String newName) {
        this.name.set(newName);
    }

    @Override
    public ResourceDirectory getDirectory() {
        return workingDirectory;
    }

    @Override
    public long getLastModified() {
        if (workingDirectory instanceof PhysicalResource) {
            return ((PhysicalResource) workingDirectory).getLastModified();
        }
        return lastModified;
    }

    @Override
    public void setLastModified(long lastModified) {
        if (workingDirectory instanceof PhysicalResource) {
            ((PhysicalResource) workingDirectory).setLastModified(lastModified);
            return;
        }
        this.lastModified = lastModified;
    }

    @Override
    public String getAuthor() {
        return author.get();
    }

    @Override
    public void setAuthor(String author) {
        this.author.set(author);
    }

    @Override
    public StringProperty impl_nameProperty() {
        return name;
    }

    @Override
    public StringProperty impl_authorProperty() {
        return author;
    }

    @Override
    public void save() {
        Resource resource = getDirectory().getChild("project.yml");
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        if (resource instanceof WritableResourceFile) {
            NodeFlow.getApplication().getProjectManager().storeProjectProperties(this, (WritableResourceFile) resource);
        }
    }
}
