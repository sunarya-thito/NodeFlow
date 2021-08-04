package thito.nodeflow.internal.editor;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import thito.nodeflow.api.editor.*;

public class UndoManagerImpl implements UndoManager {

    private ObservableList<EditorAction> actions = FXCollections.observableArrayList();
    private IntegerProperty index = new SimpleIntegerProperty();
    private IntegerProperty buffer = new SimpleIntegerProperty(500);

    private ObservableValue<EditorAction> undo, redo;
    private boolean paused;

    private void validate() {
        while (actions.size() > buffer.get()) {
            actions.remove(actions.size() - 1);
        }
        if (index.get() > actions.size()) index.set(actions.size());
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public ObservableValue<EditorAction> impl_nextUndoBinding() {
        return undo == null ? undo = Bindings.createObjectBinding(this::getNextUndo, index, actions) : undo;
    }

    @Override
    public ObservableValue<EditorAction> impl_nextRedoBinding() {
        return redo == null ? redo = Bindings.createObjectBinding(this::getNextRedo, index, actions) : redo;
    }

    @Override
    public int getBuffer() {
        return buffer.get();
    }

    @Override
    public void setBuffer(int buffer) {
        this.buffer.set(buffer);
    }

    @Override
    public EditorAction getNextUndo() {
        if (hasUndo()) {
            return actions.get(index.get());
        }
        return null;
    }

    @Override
    public EditorAction getNextRedo() {
        if (hasRedo()) {
            return actions.get(index.get() - 1);
        }
        return null;
    }

    @Override
    public EditorAction redo() {
        if (hasRedo()) {
            EditorAction action = actions.get(index.get() - 1);
            action.redo();
            index.set(index.get() - 1);
            return action;
        }
        return null;
    }

    @Override
    public EditorAction undo() {
        if (hasUndo()) {
            EditorAction action = actions.get(index.get());
            action.undo();
            index.set(index.get() + 1);
            return action;
        }
        return null;
    }

    @Override
    public boolean hasUndo() {
        validate();
        return index.get() >= 0 && index.get() < actions.size();
    }

    @Override
    public boolean hasRedo() {
        validate();
        return index.get() - 1 >= 0 && index.get() - 1 < actions.size();
    }

    @Override
    public void storeAction(EditorAction action) {
        if (paused) return;
        validate();
        actions.add(index.get(), action);
        for (int i = 0; i < index.get(); i++) actions.remove(i);
    }

    @Override
    public void storeAction(ObservableValue<String> name, Runnable undo, Runnable redo) {
        storeAction(new EditorAction() {

            @Override
            public ObservableValue<String> getDisplayName() {
                return name;
            }

            @Override
            public void undo() {
                undo.run();
            }

            @Override
            public void redo() {
                redo.run();
            }
        });
    }

    @Override
    public BooleanBinding impl_hasUndoProperty() {
        return Bindings.createBooleanBinding(this::hasUndo, index, actions);
    }

    @Override
    public BooleanBinding impl_hasRedoProperty() {
        return Bindings.createBooleanBinding(this::hasRedo, index, actions);
    }
}
