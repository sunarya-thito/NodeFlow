package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.node.shape.*;

public interface PortShape {
    PortShape
        ARROW = new ArrowShape(),
        CIRCLE = new CircleShape(),
        RHOMBUS = new RhombusShape(),
        TRIANGLE = new TriangleShape();

    Handler createHandler();
    interface Handler {
        void unbind();
        PortShape getShape();
        ObjectProperty<Color> colorProperty();
        Node impl_getPeer();
        Shape impl_getShapePeer();
    }
}
