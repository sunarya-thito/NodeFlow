package thito.nodeflow.plugin.base.blueprint.element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BlueprintClass {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Class<?>> superType = new SimpleObjectProperty<>();
    private ObservableList<Class<?>> interfaceTypes = FXCollections.observableArrayList();
    private ObservableList<Variable> variables = FXCollections.observableArrayList();
    private ObservableList<Procedure> procedures = FXCollections.observableArrayList();
}
