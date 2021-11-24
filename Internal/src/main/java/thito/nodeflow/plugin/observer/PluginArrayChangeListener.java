package thito.nodeflow.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.plugin.Plugin;

public abstract class PluginArrayChangeListener<T extends ObservableArray<T>> extends PluginObserver implements ArrayChangeListener<T> {
    public PluginArrayChangeListener() {
        super();
    }

    public PluginArrayChangeListener(Plugin plugin) {
        super(plugin);
    }
}
