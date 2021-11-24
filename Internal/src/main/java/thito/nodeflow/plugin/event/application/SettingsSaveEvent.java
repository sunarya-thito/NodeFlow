package thito.nodeflow.plugin.event.application;

import thito.nodeflow.plugin.event.*;
import thito.nodeflow.settings.SettingsProperty;

public class SettingsSaveEvent implements Event, CancellableEvent {
    private SettingsProperty<?> settingsItem;
    private boolean cancelled;

    public SettingsSaveEvent(SettingsProperty<?> settingsItem) {
        this.settingsItem = settingsItem;
    }

    public SettingsProperty<?> getSettingsItem() {
        return settingsItem;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}