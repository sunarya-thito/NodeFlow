package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.layout.*;
import thito.nodeflow.internal.ui.dashboard.pages.*;
import thito.nodeflow.library.ui.*;

public class ProjectListSkin extends Skin {
    @Component("content-list")
    VBox content;

    @Override
    protected void onLayoutLoaded() {
        content.getChildren().clear();
        for (int i = 0; i < 15; i++) {
            content.getChildren().add(new ProjectListItemSkin());
        }
    }
}
