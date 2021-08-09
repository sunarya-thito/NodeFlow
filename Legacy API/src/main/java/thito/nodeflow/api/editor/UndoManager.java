package thito.nodeflow.api.editor;

import javafx.beans.binding.*;
import javafx.beans.value.*;

import java.util.function.*;

public interface UndoManager {
    int getBuffer();

    void setBuffer(int buffer);

    EditorAction getNextUndo();

    EditorAction getNextRedo();

    EditorAction redo();

    EditorAction undo();

    boolean hasUndo();

    boolean hasRedo();

    void storeAction(EditorAction action);

    void storeAction(ObservableValue<String> name, Runnable undo, Runnable redo);

    BooleanBinding impl_hasUndoProperty();

    BooleanBinding impl_hasRedoProperty();

    ObservableValue<EditorAction> impl_nextUndoBinding();

    ObservableValue<EditorAction> impl_nextRedoBinding();
}
