package thito.nodeflow.api.ui.menu.type;

import thito.nodeflow.api.ui.menu.MenuItemType;

public interface CheckBoxMenuItemType extends MenuItemType {
    boolean isSelected();
    void setSelected(boolean selected);
}
