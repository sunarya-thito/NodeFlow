package thito.nodeflow.internal.project;

import javafx.beans.property.*;

import java.util.*;

public class UndoManager {
    private LinkedList<Modification> buffer = new LinkedList<>();
    private int index;
    private int maxBuffer;
    private ReadOnlyBooleanWrapper hasUndo = new ReadOnlyBooleanWrapper();
    private ReadOnlyBooleanWrapper hasRedo = new ReadOnlyBooleanWrapper();
    private ReadOnlyStringWrapper undoDisplayName = new ReadOnlyStringWrapper();
    private ReadOnlyStringWrapper redoDisplayName = new ReadOnlyStringWrapper();

    public UndoManager(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    public void store(Modification modification) {
        buffer.add(index, modification);
        trim();
        updateState();
    }

    public ReadOnlyStringProperty undoDisplayNameProperty() {
        return undoDisplayName.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty redoDisplayNameProperty() {
        return redoDisplayName.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty hasUndoProperty() {
        return hasUndo.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty hasRedoProperty() {
        return hasRedo.getReadOnlyProperty();
    }

    private void updateState() {
        if (index < buffer.size()) {
            hasUndo.set(true);
            undoDisplayName.bind(buffer.get(index).displayNameProperty());
        } else {
            hasUndo.set(false);
            undoDisplayName.unbind();
            undoDisplayName.set(null);
        }
        if (index > 0) {
            hasRedo.set(true);
            redoDisplayName.bind(buffer.get(index - 1).displayNameProperty());
        } else {
            hasRedo.set(false);
            redoDisplayName.unbind();
            redoDisplayName.set(null);
        }
    }

    private void trim() {
        while (buffer.size() > maxBuffer) {
            buffer.pollLast();
        }
    }

    public void undo() {
        if (index >= buffer.size()) return;
        Modification modification = buffer.get(index++);
        modification.undo();
        updateState();
    }

    public void redo() {
        if (index <= 0) return;
        Modification modification = buffer.get(--index);
        modification.redo();
        updateState();
    }

}
