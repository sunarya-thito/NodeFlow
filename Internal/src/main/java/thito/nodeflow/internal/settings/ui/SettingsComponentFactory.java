package thito.nodeflow.internal.settings.ui;

import thito.nodeflow.internal.settings.*;

public interface SettingsComponentFactory<T> {
    SettingsComponent<T> createComponent(SettingsProperty<T> property);
}
