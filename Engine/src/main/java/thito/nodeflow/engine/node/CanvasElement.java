package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.skin.*;

public abstract class CanvasElement {
    public abstract DoubleProperty xProperty();
    public abstract DoubleProperty yProperty();
    public abstract BooleanProperty selectedProperty();
    public abstract Skin getSkin();
    private final DragInfo info = new DragInfo();
    public DragInfo getDragInfo() {
        return info;
    }

    public class DragInfo {
        private double x, y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
