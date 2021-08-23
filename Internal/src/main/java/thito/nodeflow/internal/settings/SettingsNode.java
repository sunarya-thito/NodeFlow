package thito.nodeflow.internal.settings;

import javafx.scene.*;

public abstract class SettingsNode<T> {
    private SettingsProperty<T> item;

    public SettingsNode(SettingsProperty<T> item) {
        this.item = item;
    }

    public final SettingsProperty<T> getItem() {
        return item;
    }

    public abstract Node getNode();

    public abstract void apply();
}
