package thito.nodeflow.internal.ui.dashboard.pages;

import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.ui.*;

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
