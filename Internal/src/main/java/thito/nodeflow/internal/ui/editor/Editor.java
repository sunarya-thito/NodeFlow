package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.task.*;

public class Editor {
    private ObservableList<SearchableContentContext> searchableContentContexts = FXCollections.observableArrayList();
    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private ObservableList<EditorTab> openedFiles = FXCollections.observableArrayList();
    private EditorWindow editorWindow;

    public Editor() {
        editorWindow = new EditorWindow(this);
        project.addListener((obs, old, val) -> {
            if (old != null) {
                if (old.editorProperty().get() == this) {
                    old.editorProperty().set(null);
                }
                old.getProperties().closeProject();
            }
            openedFiles.removeIf(x -> x instanceof FileTab);
            if (val != null) {
                // kinda wonder if this actually gonna happen
                Editor oldEditor = val.editorProperty().get();
                if (oldEditor != null) {
                    oldEditor.projectProperty().set(null);
                }
                val.editorProperty().set(this);
            }
        });
        for (SearchableContentProvider provider : SearchManager.getInstance().getProviderList()) {
            searchableContentContexts.add(provider.createContext(this));
        }
    }

    public ObservableList<SearchableContentContext> getSearchableContentContexts() {
        return searchableContentContexts;
    }

    public FileTab openFile(Resource resource) {
        TaskThread.UI().checkThread();
        for (EditorTab x : openedFiles) {
            if (x instanceof FileTab tab) {
                if (tab.getResource().equals(resource)) {
                    tab.getTab().getTabPane().getSelectionModel().select(tab.getTab());
                    return tab;
                }
            }
        }
        FileTab tab = new FileTab(this, project.get(), resource, PluginManager.getPluginManager().getModule(resource));
        openedFiles.add(tab);
        tab.reload();
        return tab;
    }

    public ObservableList<EditorTab> getOpenedTabs() {
        return openedFiles;
    }

    public EditorWindow getEditorWindow() {
        return editorWindow;
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }
}
