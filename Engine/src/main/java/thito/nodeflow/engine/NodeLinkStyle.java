package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.scene.Node;

public interface NodeLinkStyle {
    Handler createHandler();
    interface Handler {
        DoubleProperty sourceXProperty();
        DoubleProperty sourceYProperty();
        DoubleProperty targetXProperty();
        DoubleProperty targetYProperty();
        Node getPeer();
    }
}
