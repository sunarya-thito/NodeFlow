package thito.nodeflow.library.ui.form;

import javafx.scene.*;

public interface FormNode<T> {
    Node getNode();
    void initialize(FormProperty<T> property);
}
