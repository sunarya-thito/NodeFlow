package thito.nodeflow.internal.ui.menu.type;

import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.type.*;
import thito.nodeflow.internal.ui.menu.handler.*;

public class RadioButtonMenuItemTypeImpl implements RadioButtonMenuItemType {
    public static final RadioButtonMenuItemType INSTANCE = new RadioButtonMenuItemTypeImpl();
    @Override
    public MenuItemHandler createHandler(MenuItem item) {
        return new RadioButtonMenuItemHandlerImpl(item);
    }
}
