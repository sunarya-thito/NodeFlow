package thito.nodeflow.api.editor.menu;

import javafx.beans.property.*;

public interface ToolChoice<T> extends ToolControl {
    T[] getChoices();
    T getChosen();
    ObjectProperty<T> impl_chosenProperty();
}
