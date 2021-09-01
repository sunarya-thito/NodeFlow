package thito.nodeflow.internal.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;

public abstract class PluginMapChangeListener<K, V> extends PluginObserver implements MapChangeListener<K, V> {
    public PluginMapChangeListener() {
        super();
    }

    public PluginMapChangeListener(Plugin plugin) {
        super(plugin);
    }
}
