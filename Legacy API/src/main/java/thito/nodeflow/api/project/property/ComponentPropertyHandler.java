package thito.nodeflow.api.project.property;

import javafx.beans.property.*;
import javafx.scene.*;

public interface ComponentPropertyHandler<T> {
    BooleanProperty disableProperty();
    Node impl_getPeer();
}
