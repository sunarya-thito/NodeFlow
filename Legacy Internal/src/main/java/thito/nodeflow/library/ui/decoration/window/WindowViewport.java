package thito.nodeflow.library.ui.decoration.window;

import javafx.scene.*;
import javafx.scene.layout.*;

public class WindowViewport {

    private BorderPane pane = new BorderPane();

    BorderPane getPane() {
        return pane;
    }

    public void setComponent(Node node) {
        pane.setCenter(node);
    }

    public Node getComponent() {
        return pane.getCenter();
    }
}
