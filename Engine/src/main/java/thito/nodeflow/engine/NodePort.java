package thito.nodeflow.engine;

import javafx.scene.paint.*;

public class NodePort {
    private boolean multiple;
    private Color color;
    private PortShape shape;

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
