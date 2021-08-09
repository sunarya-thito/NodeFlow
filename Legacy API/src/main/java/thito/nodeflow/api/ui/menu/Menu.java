package thito.nodeflow.api.ui.menu;

import javafx.scene.Node;

import java.util.List;

public interface Menu {
    List<MenuItem> getItems();
    Node impl_createPeer();
}
