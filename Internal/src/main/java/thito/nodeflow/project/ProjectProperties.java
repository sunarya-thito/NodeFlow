package thito.nodeflow.project;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.config.*;
import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.settings.Settings;
import thito.nodeflow.task.TaskThread;

import java.io.*;
import java.util.stream.*;

public class ProjectProperties {

    private Workspace workspace;
    private Resource directory;
    private StringProperty name = TaskThread.IO().watch(new SimpleStringProperty());
    private LongProperty lastModified = TaskThread.IO().watch(new SimpleLongProperty(System.currentTimeMillis()));
    private StringProperty description = TaskThread.IO().watch(new SimpleStringProperty());
    private ObservableList<String> tags = TaskThread.IO().watch(FXCollections.observableArrayList());

    private Section config;

    @IOThread
    public ProjectProperties(Workspace workspace, Resource directory, Section config) {
        this.workspace = workspace;
        this.config = config;
        this.directory = directory;
        name.set(config.getString(new Path("name")).orElseThrow(() -> new NullPointerException("name")));
        description.set(config.getString(new Path("description")).orElse(""));
        lastModified.set(config.getLong(new Path("last-modified")).orElse(System.currentTimeMillis()));
        tags.setAll(config.getList(new Path("tags")).stream().map(String::valueOf).collect(Collectors.toList()));
        name.addListener(obs -> save());
        description.addListener(obs -> save());
        lastModified.addListener(obs -> save());
        tags.addListener((InvalidationListener) obs -> save());
        validate();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public LongProperty lastModifiedProperty() {
        return lastModified;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Resource getDirectory() {
        return directory;
    }

    public Section getConfiguration() {
        return config;
    }

    public long getLastModified() {
        return lastModified.get();
    }

    @IOThread
    public void setLastModified(long lastModified) {
        this.lastModified.set(lastModified);
    }

    public String getName() {
        return name.get();
    }

    @IOThread
    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    @IOThread
    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<String> getTags() {
        return tags;
    }

    void save() {
        directory.toFile().mkdirs();
        config.set(new Path("name"), getName());
        config.set(new Path("description"), getDescription());
        config.set(new Path("last-modified"), getLastModified());
        config.set(new Path("tags"), getTags());
        Settings.saveProjectSettings(this);
        TaskThread.IO().schedule(() -> {
            try (Writer writer = directory.getChild("project.yml").openWriter()) {
                writer.write(Section.toString(config));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void validate() {
        if (config.getString(new Path("name")).isEmpty()) throw new IllegalArgumentException("invalid project properties");
    }
}
