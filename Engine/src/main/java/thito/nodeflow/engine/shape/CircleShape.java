package thito.nodeflow.engine.shape;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.*;

public class CircleShape implements PortShape {
    @Override
    public Handler createHandler() {
        return new Handler() {
            private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.TRANSPARENT);
            private Pane wrapper;
            private Circle shape;

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
