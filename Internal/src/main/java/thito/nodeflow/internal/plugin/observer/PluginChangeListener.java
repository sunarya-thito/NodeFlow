package thito.nodeflow.internal.plugin.observer;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;

public abstract class PluginChangeListener<T> extends PluginObserver implements ListChangeListener<T> {

    public PluginChangeListener() {
        super();
    }

    public PluginChangeListener(Plugin plugin) {
        super(plugin);
    }

}
