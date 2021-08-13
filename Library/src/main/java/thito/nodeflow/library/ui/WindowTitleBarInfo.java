package thito.nodeflow.library.ui;

import javafx.geometry.*;

public class WindowTitleBarInfo {
    private BoundingBox dragBox = new BoundingBox(0, 0, 0, 0);

    public void setDragBox(BoundingBox dragBox) {
        this.dragBox = dragBox;
    }

    public BoundingBox getDragBox() {
        return dragBox;
    }
}
