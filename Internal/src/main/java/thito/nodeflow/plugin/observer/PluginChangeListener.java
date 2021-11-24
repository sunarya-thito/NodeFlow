package thito.nodeflow.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.plugin.Plugin;

public abstract class PluginChangeListener<T> extends PluginObserver implements ListChangeListener<T> {

    public PluginChangeListener() {
        super();
    }

    public PluginChangeListener(Plugin plugin) {
        super(plugin);
    }

}
