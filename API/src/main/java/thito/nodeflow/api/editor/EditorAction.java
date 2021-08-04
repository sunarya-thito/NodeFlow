package thito.nodeflow.api.editor;

import javafx.beans.value.*;
import thito.nodeflow.api.locale.*;

import java.util.function.*;

public interface EditorAction {
    static void store(FileSession project, ObservableValue<String> name, Runnable undo, Runnable redo) {
        project.getUndoManager()
                .storeAction(name, undo, redo);
    }

    static void store(FileSession project, I18nItem name, Runnable undo, Runnable redo) {
        store(project, name.stringBinding(), undo, redo);
    }

    ObservableValue<String> getDisplayName();

    void undo();

    void redo();
}
