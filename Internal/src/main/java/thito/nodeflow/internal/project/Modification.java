package thito.nodeflow.internal.project;

import javafx.beans.value.*;

public interface Modification {
    ObservableValue<String> displayNameProperty();
    void undo();
    void redo();
}
