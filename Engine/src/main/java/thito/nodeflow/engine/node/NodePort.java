package thito.nodeflow.engine.node;

import javafx.scene.paint.*;

public class NodePort {
    private final boolean multiple;
    private final Color color;
    private final PortShape shape;

    public NodePort(boolean multiple, Color color, PortShape shape) {
        this.multiple = multiple;
        this.color = color;
        this.shape = shape;
    }

    public PortShape getShape() {
        return shape;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public Color getColor() {
        return color;
    }
}
