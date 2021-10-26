package thito.nodeflow.plugin.base.blueprint.element;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.plugin.base.blueprint_legacy.BlueprintHandler;

public class Procedure {
    private boolean isOverride;
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<LocalVariable> localVariables = FXCollections.observableArrayList();
    private ObservableList<Parameter> inputs = FXCollections.observableArrayList();
    private BlueprintHandler handler;

    public ObservableList<LocalVariable> getLocalVariables() {
        return localVariables;
    }

    public ObservableList<Parameter> getInputs() {
        return inputs;
    }
}
