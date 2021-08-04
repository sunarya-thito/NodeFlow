package thito.nodeflow.internal.ui.menu.handler;

import javafx.scene.*;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.handler.*;
import thito.nodeflow.internal.ui.menu.type.*;

public class ButtonMenuItemHandlerImpl implements ButtonMenuItemHandler {
    private MenuItem item;
    public ButtonMenuItemHandlerImpl(MenuItem item) {
        this.item = item;
    }

    @Override
    public MenuItemType getType() {
        return ButtonMenuItemTypeImpl.INSTANCE;
    }

    @Override
    public Node impl_createPeer() {
        return null;
    }

    @Override
    public void handleDispatch() {
    }
}
