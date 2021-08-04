package thito.nodeflow.internal.editor.menu;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.menu.*;

public class ToolSeparatorImpl implements ToolComponent {

    private HBox box;
    @Override
    public Node impl_getPeer() {
        if (box == null) {
            HBox shape = new HBox();
            shape.getStyleClass().add("tool-separator-shape");
            box = new HBox(shape);
            box.getStyleClass().add("tool-separator");
        }
        return box;
    }

}
