package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;

public class ProjectBoxSkin extends Skin {

    @Component("project-name")
    Labeled name;

    @Component("description")
    Labeled description;

    @Component("size")
    Labeled size;

    @Component("tags")
    Pane tags;

    @Component("project-icon")
    ImagePane icon;

    @Override
    protected void onLayoutLoaded() {
        icon.setImage(ProjectHelper.create(name.getText(), 150, 100));
    }
}
