package thito.nodeflow.internal.project;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.config.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.annotation.BGThread;
import thito.nodeflow.internal.annotation.IOThread;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.task.*;

import java.io.*;
import java.util.stream.*;

public class ProjectProperties {
    public static boolean isProjectFolder(Resource directory) {
        return directory.getChild("project.yml").exists();
    }

    private Workspace workspace;
    private Resource directory;
    private StringProperty name = TaskThread.IO().lock(new SimpleStringProperty());
    private LongProperty lastModified = TaskThread.IO().lock(new SimpleLongProperty(System.currentTimeMillis()));
    private StringProperty description = TaskThread.IO().lock(new SimpleStringProperty());
    private ObservableList<String> tags = TaskThread.IO().lock(FXCollections.observableArrayList());
    private ObjectProperty<Project> loadedProject = TaskThread.IO().lock(new SimpleObjectProperty<>());

    private Section config;

    @IOThread
    public ProjectProperties(Workspace workspace, Resource directory, Section config) {
        this.workspace = workspace;
        this.config = config;
        this.directory = directory;
        name.set(config.getString("name").orElseThrow(() -> new NullPointerException("name")));
        description.set(config.getString("description").orElse(""));
        lastModified.set(config.getLong("last-modified").orElse(System.currentTimeMillis()));
        tags.setAll(config.getList("tags").stream().map(String::valueOf).collect(Collectors.toList()));
        name.addListener(obs -> save());
        description.addListener(obs -> save());
        lastModified.addListener(obs -> save());
        tags.addListener((InvalidationListener) obs -> save());
        validate();
    }

//    @BGThread
//    public void openProject() {
//        TaskThread.BACKGROUND().checkThread();
////        Project oldProject = loadedProject.get();
////        Editor oldEditor = null;
////        if (oldProject != null) {
////            oldEditor = oldProject.editorProperty().get();
////            if (oldEditor != null) {
////                if (oldEditor.projectProperty().get() == oldProject) {
////                    Editor finalOldEditor1 = oldEditor;
////                    TaskThread.UI().schedule(() -> {
////                        finalOldEditor1.projectProperty().set(null);
////                    });
////                }
////            }
////        }
//        closeProject();
//        Project project = new Project(getWorkspace(), this);
//        loadedProject.set(project);
//        if (oldEditor != null && oldEditor.projectProperty().get() == null) {
//            oldEditor.projectProperty().set(project);
//            Editor finalOldEditor = oldEditor;
//            TaskThread.UI().schedule(() -> {
//                finalOldEditor.getEditorWindow().getStage().toFront();
//            });
//        } else {
//            for (Editor editor : NodeFlow.getInstance().getActiveEditors()) {
//                if (editor.projectProperty().get() == null) {
//                    TaskThread.UI().schedule(() -> {
//                        editor.projectProperty().set(project);
//                    });
//                    return project;
//                }
//            }
//            TaskThread.UI().schedule(() -> {
//                Editor editor = NodeFlow.getInstance().createNewEditor();
//                editor.projectProperty().set(project);
//            });
//        }
//        return project;
//    }

//    @BGThread
//    public void closeProject() {
//        TaskThread.BACKGROUND().checkThread();
//        Project project = loadedProject.get();
//        if (project != null) {
//            ObservableList<Editor> activeEditors = NodeFlow.getInstance().getActiveEditors();
//            for (Editor editor : activeEditors) {
//                if (editor.projectProperty().get() == project) {
//                    if (activeEditors.size() == 1) {
//                        TaskThread.UI().schedule(() -> {
//                            editor.projectProperty().set(null);
//                        });
//                    } else {
//                        TaskThread.UI().schedule(() -> {
//                            editor.getEditorWindow().close();
//                        });
//                    }
//                    break;
//                }
//            }
//            setLastModified(System.currentTimeMillis()); // triggers to save
//            Settings.unloadProjectSettings(this);
//        }
//        loadedProject.set(null);
//    }

    @IOThread
    public ObjectProperty<Project> loadedProjectProperty() {
        return loadedProject;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @IOThread
    public StringProperty nameProperty() {
        return name;
    }

    @IOThread
    public LongProperty lastModifiedProperty() {
        return lastModified;
    }

    @IOThread
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

    @IOThread
    public ObservableList<String> getTags() {
        return tags;
    }

    void save() {
        directory.toFile().mkdirs();
        config.set("name", getName());
        config.set("description", getDescription());
        config.set("last-modified", getLastModified());
        config.set("tags", getTags());
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
        if (config.getString("name").isEmpty()) throw new IllegalArgumentException("invalid project properties");
    }
}
