package thito.nodeflow.plugin.base.blueprint.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public abstract class Function {
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<Parameter> parameterList = FXCollections.observableArrayList();

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    public ObservableList<Parameter> getParameterList() {
        return parameterList;
    }
}
