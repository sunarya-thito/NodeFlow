package thito.nodeflow.internal.plugin.event.application;

import thito.nodeflow.internal.plugin.event.*;
import thito.nodeflow.internal.settings.*;

public class SettingsSaveEvent implements Event {
    private SettingsProperty<?> settingsItem;

    public SettingsSaveEvent(SettingsProperty<?> settingsItem) {
        this.settingsItem = settingsItem;
    }

    public SettingsProperty<?> getSettingsItem() {
        return settingsItem;
    }

}
