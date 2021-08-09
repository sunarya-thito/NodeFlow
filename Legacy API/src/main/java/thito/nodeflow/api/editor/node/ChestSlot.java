package thito.nodeflow.api.editor.node;

import javafx.scene.layout.*;

public interface ChestSlot {
    ChestItem getItem();
    void setItem(ChestItem item);
    BorderPane impl_getPeer();
}
