package thito.nodeflow.settings.ui;

import thito.nodeflow.settings.SettingsProperty;

public interface SettingsComponentFactory<T> {
    SettingsComponent<T> createComponent(SettingsProperty<T> property);
}
