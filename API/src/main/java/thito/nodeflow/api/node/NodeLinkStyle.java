package thito.nodeflow.api.node;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;

public enum NodeLinkStyle implements Describable, Iconable {
    LINEAR("node-style-linear", "line-linear"), CURVE("node-style-curve", "line-curve"), PIPE("node-style-pipe", "line-pipe");

    private String name;
    private Icon icon;

    NodeLinkStyle(String name, String icon) {
        this.name = name;
        this.icon = Icon.icon(icon);
    }

    @Override
    public String getDescribedName() {
        return I18n.$(name).getString();
    }

    @Override
    public Icon getIcon() {
        return icon;
    }
}
