package thito.nodeflow.internal.ui.menu.type;

import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.type.*;
import thito.nodeflow.internal.ui.menu.handler.*;

public class CheckBoxMenuItemTypeImpl implements CheckBoxMenuItemType {
    public static CheckBoxMenuItemType INSTANCE = new CheckBoxMenuItemTypeImpl();
    private boolean selected;
    @Override
    public MenuItemHandler createHandler(MenuItem item) {
        return new CheckBoxMenuItemHandlerImpl(item);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
