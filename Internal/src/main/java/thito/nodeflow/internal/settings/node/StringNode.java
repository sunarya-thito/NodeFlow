package thito.nodeflow.internal.settings.node;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;

public class StringNode extends SettingsNode<String> {
    private TextField field;

    public StringNode(SettingsProperty<String> item) {
        super(item);
        field = new TextField(item.get());
        field.getStyleClass().add("string-node");
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
