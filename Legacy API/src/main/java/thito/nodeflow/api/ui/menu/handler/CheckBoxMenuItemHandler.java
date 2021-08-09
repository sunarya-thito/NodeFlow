package thito.nodeflow.api.ui.menu.handler;

import javafx.beans.property.BooleanProperty;
import thito.nodeflow.api.ui.menu.MenuItemHandler;

public interface CheckBoxMenuItemHandler extends MenuItemHandler {
    boolean isSelected();
    void setSelected(boolean selected);
    BooleanProperty impl_selectedProperty();
}
