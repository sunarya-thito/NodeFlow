package thito.nodeflow.api.editor.menu;

import javafx.beans.property.*;

public interface ToolControl extends ToolComponent {
    boolean isDisabled();
    void setDisabled(boolean disabled);
    BooleanProperty impl_disableProperty();
}
