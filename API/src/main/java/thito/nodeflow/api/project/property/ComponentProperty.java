package thito.nodeflow.api.project.property;

import javafx.beans.property.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.property.*;

public interface ComponentProperty<T> {
    BooleanProperty disableProperty();

    I18nItem getName();

    Property<T> valueProperty();

    ComponentPropertyHandler<T> getHandler();

    Object impl_getPeer();
}
