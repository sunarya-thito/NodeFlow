package thito.nodeflow.api.ui.menu;

import javafx.scene.Node;

public interface MenuItemHandler {
    MenuItemType getType();
    Node impl_createPeer();
    void handleDispatch();
}
