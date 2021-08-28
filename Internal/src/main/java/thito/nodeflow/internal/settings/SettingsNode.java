package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import javafx.scene.*;

public abstract class SettingsNode<T> {
    private SettingsProperty<T> item;
    private BooleanProperty hasChangedProperty = new SimpleBooleanProperty();

    public SettingsNode(SettingsProperty<T> item) {
        this.item = item;
    }

    public BooleanProperty hasChangedPropertyProperty() {
        return hasChangedProperty;
    }

    public final SettingsProperty<T> getItem() {
        return item;
    }

    public abstract Node getNode();

    public abstract void apply();
}
