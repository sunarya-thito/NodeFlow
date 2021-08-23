package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import thito.nodeflow.internal.project.*;

public class Editor {
    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private EditorWindow editorWindow;

    public Editor() {
        editorWindow = new EditorWindow(this);
    }

    public EditorWindow getEditorWindow() {
        return editorWindow;
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }
}
