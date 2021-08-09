package thito.nodeflow.engine.shape;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.*;

public class RhombusShape implements PortShape {
    @Override
    public Handler createHandler() {
        return new Handler() {
            private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.TRANSPARENT);
            private Pane wrapper;
            private Polygon shape;

            {
                shape = new Polygon(
                        11, 2,
                        20, 11,
                        11, 20,
                        2, 11
                );
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
                return RhombusShape.this;
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
