package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.control.*;
import javafx.scene.text.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

public class VersionSkin extends Skin {
    @Component("version")
    Text version;

    @Component("changelog-content")
    Label description;

    Version v;

    public VersionSkin(Version v) {
        this.v = v;
    }

    @Override
    protected void onLayoutLoaded() {
        version.setText(v.getVersion());
        description.setText(v.getChangeLog());
    }
}
