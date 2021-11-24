package thito.nodeflow.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.plugin.Plugin;

public abstract class PluginMapChangeListener<K, V> extends PluginObserver implements MapChangeListener<K, V> {
    public PluginMapChangeListener() {
        super();
    }

    public PluginMapChangeListener(Plugin plugin) {
        super(plugin);
    }
}
