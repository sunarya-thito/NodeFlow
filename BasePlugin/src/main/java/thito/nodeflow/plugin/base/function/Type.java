package thito.nodeflow.plugin.base.function;

import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

public interface Type {
    String getName();
    String getSimpleName();
    boolean isAssignableFrom(Type type);
    ObjectProperty<Color> colorProperty();
}
