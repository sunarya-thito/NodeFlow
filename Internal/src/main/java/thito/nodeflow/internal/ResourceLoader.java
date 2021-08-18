package thito.nodeflow.internal;

import javafx.beans.property.*;

public class ResourceLoader {
    private DoubleProperty progress = new SimpleDoubleProperty();

    public DoubleProperty progressProperty() {
        return progress;
    }
}
