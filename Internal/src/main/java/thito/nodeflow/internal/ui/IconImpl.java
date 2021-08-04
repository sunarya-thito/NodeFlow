package thito.nodeflow.internal.ui;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;

public class IconImpl extends ImageImpl implements Icon {
    private String name;
    private Theme theme;
    public IconImpl(String name, Theme theme) {
        load(name, theme);
    }

    protected void load(String name, Theme theme) {
        this.name = name;
        this.theme = theme;
        Resource themedIcon = NodeFlow.getApplication().getResourceManager().getResource("icons/"+theme.getIconDirectoryPath()+"/"+name+".png");
        if (themedIcon instanceof ResourceFile && !(themedIcon instanceof UnknownResource)) {
            load((ResourceFile) themedIcon);
            return;
        }
        Resource defaultIcon = NodeFlow.getApplication().getResourceManager().getResource("icons/"+name+".png");
        if (defaultIcon instanceof ResourceFile && !(defaultIcon instanceof UnknownResource)) {
            load((ResourceFile) defaultIcon);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        load(name, theme);
    }
}
