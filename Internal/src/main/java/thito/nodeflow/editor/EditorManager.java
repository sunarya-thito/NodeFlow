package thito.nodeflow.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.editor.content_legacy.EditorContentType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EditorManager {
    private static final ObservableList<Editor> activeEditors = FXCollections.observableArrayList();
    private static final Set<EditorContentType> editorElements = new HashSet<>();

    @BGThread
    public static ObservableList<Editor> getActiveEditors() {
        return activeEditors;
    }

    @BGThread
    public static EditorContentType getEditorElement(String id) {
        return editorElements.stream().filter(x -> x.id().equals(id)).findAny().orElse(null);
    }

    @BGThread
    public static void registerEditorElement(EditorContentType editorContentType) {
        editorElements.add(editorContentType);
    }

    @BGThread
    public static void unregisterEditorElement(EditorContentType editorContentType) {
        editorElements.remove(editorContentType);
    }

    @BGThread
    public static Set<EditorContentType> getEditorElements() {
        return Collections.unmodifiableSet(editorElements);
    }
}
