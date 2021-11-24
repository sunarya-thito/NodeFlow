package thito.nodeflow.ui.dashboard.pages;

import javafx.scene.layout.*;
import thito.nodeflow.Version;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.dashboard.*;

public class ChangeLogPage extends DashboardPage {

    @Component("content")
    private Pane content;

    @Override
    protected void onLayoutLoaded() {
        content.getChildren().clear();
        for (Version v : Version.getVersions()) {
            content.getChildren().add(new VersionSkin(v));
        }
    }
}
