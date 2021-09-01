package thito.nodeflow.internal.plugin.observer;

import javafx.beans.*;
import thito.nodeflow.internal.plugin.*;

import java.lang.ref.*;

public abstract class PluginObserver implements WeakListener {
    private final WeakReference<Plugin> plugin;

    public PluginObserver() {
        this.plugin = new WeakReference<>(Plugin.getPlugin(getClass()));
    }

    public PluginObserver(Plugin plugin) {
        this.plugin = new WeakReference<>(plugin);
    }

    @Override
    public boolean wasGarbageCollected() {
        Plugin plugin = getPlugin();
        return plugin == null || !plugin.getPluginSettings().getEnabled().get();
    }

    public Plugin getPlugin() {
        return plugin.get();
    }
}
