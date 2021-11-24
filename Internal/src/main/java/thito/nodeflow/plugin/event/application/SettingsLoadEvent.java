package thito.nodeflow.plugin.event.application;

import thito.nodeflow.plugin.event.*;
import thito.nodeflow.settings.SettingsProperty;

public class SettingsLoadEvent implements Event {
    private SettingsProperty<?> settingsItem;

    public SettingsLoadEvent(SettingsProperty<?> settingsItem) {
        this.settingsItem = settingsItem;
    }

    public SettingsProperty<?> getSettingsItem() {
        return settingsItem;
    }

}
