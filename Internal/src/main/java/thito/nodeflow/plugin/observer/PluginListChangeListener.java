package thito.nodeflow.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.plugin.Plugin;

public abstract class PluginListChangeListener<T> extends PluginObserver implements ListChangeListener<T> {
    public PluginListChangeListener() {
        super();
    }

    public PluginListChangeListener(Plugin plugin) {
        super(plugin);
    }
}
