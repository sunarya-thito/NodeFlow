package thito.nodeflow.internal.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;

public abstract class PluginListChangeListener<T> extends PluginObserver implements ListChangeListener<T> {
    public PluginListChangeListener() {
        super();
    }

    public PluginListChangeListener(Plugin plugin) {
        super(plugin);
    }
}
