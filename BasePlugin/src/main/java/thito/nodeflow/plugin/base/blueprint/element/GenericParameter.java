package thito.nodeflow.plugin.base.blueprint.element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Type;

public class GenericParameter {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Type> superType = new SimpleObjectProperty<>();
    private ObservableList<Type> interfaceTypes = FXCollections.observableArrayList();
}
