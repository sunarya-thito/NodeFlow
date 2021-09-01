package thito.nodeflow.internal.settings.node;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;

public class StringNode extends SettingsNode<String> {
    private TextField field;
    private ObjectProperty<String> value = new SimpleObjectProperty<>();
    public StringNode(SettingsProperty<String> item) {
        super(item);
        field = new TextField(item.get());
        field.getStyleClass().add("string-node");
        value.bind(field.textProperty());
        hasChangedProperty().bind(value.isNotEqualTo(item));
    }

    @Override
    public Node getNode() {
        return field;
    }

    @Override
    public void apply() {
        getItem().set(field.getText());
    }
}
