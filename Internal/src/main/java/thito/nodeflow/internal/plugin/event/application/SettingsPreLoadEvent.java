package thito.nodeflow.internal.plugin.event.application;

import thito.nodeflow.internal.plugin.event.*;
import thito.nodeflow.internal.settings.*;

public class SettingsPreLoadEvent implements CancellableEvent {
    private SettingsProperty<?> settingsItem;
    private boolean cancelled;

    public SettingsPreLoadEvent(SettingsProperty<?> settingsItem) {
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
