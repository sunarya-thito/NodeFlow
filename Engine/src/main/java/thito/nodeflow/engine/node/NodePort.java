package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import javafx.scene.paint.*;

public class NodePort {
    private final boolean multiple;
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final PortShape shape;

    public NodePort(boolean multiple, Color color, PortShape shape) {
        this.multiple = multiple;
        this.color.set(color);
        this.shape = shape;
    }

    public PortShape getShape() {
        return shape;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color.get();
    }
}
