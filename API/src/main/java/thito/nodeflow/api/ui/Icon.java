package thito.nodeflow.api.ui;

import thito.nodeflow.api.*;

public interface Icon extends Image {
    static Icon icon(String name) {
        return NodeFlow.getApplication().getResourceManager().getIcon(name);
    }
    void applyTheme(Theme theme);
}
