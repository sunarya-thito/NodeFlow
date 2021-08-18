package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

public class ProjectListItemSkin extends Skin {
    @Component("name")
    Label name;
    @Component("description")
    Label description;
    @Component("tags")
    FlowPane tags;

    @Override
    protected void onLayoutLoaded() {
        name.setText("Project Name");
        description.setText("Lorem ipsum dolor sit amet");
    }
}
