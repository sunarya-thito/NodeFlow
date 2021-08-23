package thito.nodeflow.internal.project;

import thito.nodeflow.library.config.*;
import thito.nodeflow.library.resource.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class ProjectProperties {
    public static boolean isProjectFolder(Resource directory) {
        return directory.getChild("project.yml").exists();
    }

    private Resource directory;
    private Section config;

    public ProjectProperties(Resource directory) throws IOException {
        this.directory = directory;
        try (Reader reader = directory.getChild("project.yml").openReader()) {
            config = Section.parseToMap(reader);
        }
        validate();
    }

    public Resource getDirectory() {
        return directory;
    }

    public Section getConfig() {
        return config;
    }

    public long getLastModified() {
        return config.getLong("last-modified").orElse(System.currentTimeMillis());
    }

    public void setLastModified(long lastModified) {
        config.set("last-modified", lastModified);
        save();
    }

    public String getName() {
        return config.getString("name").orElseThrow();
    }

    public void setName(String name) {
        config.set("name", name);
        save();
    }

    public String getDescription() {
        return config.getString("description").orElse(null);
    }

    public void setDescription(String description) {
        config.set("description", description);
        save();
    }

    public List<String> getTags() {
        return config.getList("tags").orElse(new ListSection()).stream().map(String::valueOf).collect(Collectors.toList());
    }

    public void setTags(List<String> tags) {
        config.set("tags", tags);
        save();
    }

    void save() {
        directory.toFile().mkdirs();
        try (Writer writer = directory.getChild("project.yml").openWriter()) {
            writer.write(Section.toString(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void validate() {
        if (!config.getString("name").isPresent()) throw new IllegalArgumentException("invalid project properties");
    }
}
