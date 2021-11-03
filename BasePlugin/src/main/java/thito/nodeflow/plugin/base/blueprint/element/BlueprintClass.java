package thito.nodeflow.plugin.base.blueprint.element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.java.IClass;
import thito.nodeflow.plugin.base.blueprint.element.state.BlueprintClassState;

public class BlueprintClass {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<IClass> superType = new SimpleObjectProperty<>();
    private ObservableList<IClass> interfaceTypes = FXCollections.observableArrayList();
    private ObservableList<Variable> variables = FXCollections.observableArrayList();
    private ObservableList<Procedure> procedures = FXCollections.observableArrayList();

    public void loadState(BlueprintClassState state) {
        state.name = name.get();
        state.extensionTypeName = superType.getName();
    }

    public void saveState(BlueprintClassState state) {

    }
}
