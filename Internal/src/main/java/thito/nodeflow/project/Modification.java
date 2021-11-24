package thito.nodeflow.project;

import javafx.beans.value.*;

public interface Modification {
    ObservableValue<String> displayNameProperty();
    void undo();
    void redo();
}
