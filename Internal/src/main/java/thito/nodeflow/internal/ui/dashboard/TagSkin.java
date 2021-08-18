package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

public class TagSkin extends Skin {

    @Component("tag")
    Label view;

    private Tag tag;

    public TagSkin(Tag tag) {
        this.tag = tag;
    }

    @Override
    protected void onLayoutLoaded() {
    }
}
