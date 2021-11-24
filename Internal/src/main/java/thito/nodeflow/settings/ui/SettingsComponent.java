package thito.nodeflow.settings.ui;

import javafx.scene.*;
import thito.nodeflow.settings.SettingsProperty;

public abstract class SettingsComponent<T> {

    private SettingsProperty<T> property;
    public SettingsComponent(SettingsProperty<T> property) {
        this.property = property;
    }

    public SettingsProperty<T> valueProperty() {
        return property;
    }

    public abstract Node getNode();

}
