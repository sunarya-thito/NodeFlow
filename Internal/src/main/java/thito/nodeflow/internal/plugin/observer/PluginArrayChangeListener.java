package thito.nodeflow.internal.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;

public abstract class PluginArrayChangeListener<T extends ObservableArray<T>> extends PluginObserver implements ArrayChangeListener<T> {
    public PluginArrayChangeListener() {
        super();
    }

    public PluginArrayChangeListener(Plugin plugin) {
        super(plugin);
    }
}
