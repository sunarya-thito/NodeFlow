package thito.nodeflow.internal.settings;

import javafx.scene.*;

public abstract class SettingsNode<T> {
    private SettingsProperty<? extends T> item;

    public SettingsNode(SettingsProperty<? extends T> item) {
        this.item = item;
    }

    public final SettingsProperty<? extends T> getItem() {
        return item;
    }

    public abstract Node getNode();
}
