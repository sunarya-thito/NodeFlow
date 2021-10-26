package thito.nodeflow.plugin.base.blueprint.element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.lang.reflect.Type;

public class LocalVariable {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Type> type = new SimpleObjectProperty<>();
}
