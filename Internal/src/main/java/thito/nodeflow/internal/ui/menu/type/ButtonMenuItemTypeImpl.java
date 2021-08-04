package thito.nodeflow.internal.ui.menu.type;

import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.type.*;
import thito.nodeflow.internal.ui.menu.handler.*;

public class ButtonMenuItemTypeImpl implements ButtonMenuItemType {
    public static final ButtonMenuItemType INSTANCE = new ButtonMenuItemTypeImpl();
    @Override
    public MenuItemHandler createHandler(MenuItem item) {
        return new ButtonMenuItemHandlerImpl(item);
    }
}
