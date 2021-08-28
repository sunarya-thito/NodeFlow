package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;

public class Editor {
    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private ObservableList<FileTab> openedFiles = FXCollections.observableArrayList();
    private EditorWindow editorWindow;

    public Editor() {
        editorWindow = new EditorWindow(this);
        project.addListener((obs, old, val) -> {
            if (old != null) {
                if (old.editorProperty().get() == this) {
                    old.editorProperty().set(null);
                }
            }
            openedFiles.clear();
            if (val != null) {
                // kinda wonder if this actually gonna happen
                Editor oldEditor = val.editorProperty().get();
                if (oldEditor != null) {
                    oldEditor.projectProperty().set(null);
                }
                val.editorProperty().set(this);

            }
        });
    }

    public FileTab openFile(Resource resource) {
        TaskThread.UI().checkThread();
        for (FileTab tab : openedFiles) {
            if (tab.getResource().equals(resource)) {
                tab.getTab().getTabPane().getSelectionModel().select(tab.getTab());
                return tab;
            }
        }
        FileTab tab = new FileTab(this, project.get(), resource, PluginManager.getPluginManager().getModule(resource));
        openedFiles.add(tab);
        tab.reload();
        return tab;
    }

    public ObservableList<FileTab> getOpenedFiles() {
        return openedFiles;
    }

    public EditorWindow getEditorWindow() {
        return editorWindow;
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }
}
