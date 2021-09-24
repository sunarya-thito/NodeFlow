package thito.nodeflow.internal.settings.ui.component;

import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.ui.*;

public class BooleanComponent extends SettingsComponent<Boolean> {
    public static class Factory implements SettingsComponentFactory<Boolean> {
        @Override
        public SettingsComponent<Boolean> createComponent(SettingsProperty<Boolean> property) {
            return new BooleanComponent(property);
        }
    }

    private CheckBox node;

    public BooleanComponent(SettingsProperty<Boolean> property) {
        super(property);
        node = new CheckBox();
        node.selectedProperty().bindBidirectional(property);
    }

    @Override
    public CheckBox getNode() {
        return node;
    }
}
