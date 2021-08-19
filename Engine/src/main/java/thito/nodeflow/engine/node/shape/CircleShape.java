package thito.nodeflow.engine.node.shape;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.node.*;

public class CircleShape implements PortShape {
    @Override
    public Handler createHandler() {
        return new Handler() {
            private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.TRANSPARENT);
            private final Pane wrapper;
            private final Circle shape;

            {
                shape = new Circle(7.5);
                shape.setCenterX(shape.radiusProperty().get());
                shape.setCenterY(shape.radiusProperty().get());
                shape.fillProperty().bind(color);
                wrapper = new Pane(shape);
            }

            @Override
            public void unbind() {
                shape.fillProperty().unbind();
            }

            @Override
            public Shape impl_getShapePeer() {
                return shape;
            }

            @Override
            public PortShape getShape() {
                return CircleShape.this;
            }

            @Override
            public ObjectProperty<Color> colorProperty() {
                return color;
            }

            @Override
            public Node impl_getPeer() {
                return wrapper;
            }
        };
    }
}
