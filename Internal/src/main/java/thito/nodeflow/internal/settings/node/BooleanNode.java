package thito.nodeflow.internal.settings.node;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;

public class BooleanNode extends SettingsNode<Boolean> {

    public static class Factory implements SettingsNodeFactory<Boolean> {
        @Override
        public SettingsNode<Boolean> createNode(SettingsProperty<Boolean> item) {
            return new BooleanNode(item);
        }
    }

    private CheckBox checkBox;
    public BooleanNode(SettingsProperty<Boolean> item) {
        super(item);
        checkBox = new CheckBox();
        checkBox.getStyleClass().add("settings-boolean");
        checkBox.setSelected(item.get());

        hasChangedProperty().bind(checkBox.selectedProperty().asObject().isNotEqualTo(item));
    }

    @Override
    public void apply() {
        getItem().set(checkBox.isSelected());
    }

    @Override
    public Node getNode() {
        return checkBox;
    }
}
