package thito.nodeflow.plugin.base.blueprint.node;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Parameter {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty label = new SimpleStringProperty();
    private StringProperty hover = new SimpleStringProperty();
}
