package thito.nodeflow.ui.dashboard;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import thito.nodeflow.Version;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.dashboard.pages.*;
import thito.nodeflow.ui.Skin;

public class DashboardSkin extends Skin {

    @Component("dashboard-header")
    Pane header;

    @Component("dashboard-page")
    BorderPane page;

    @Component("close-button")
    Button button;

    @Component("software-version")
    Text text;

    @Component("dashboard-viewport")
    Pane viewport;

    @Override
    protected void onLayoutLoaded() {
        text.setText(Version.getCurrentVersion().getVersion());
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("dashboard.changelogs", ActionEvent.ACTION, event -> {
            page.setCenter(new ChangeLogPage());
        });
        registerActionFilter("dashboard.projects", ActionEvent.ACTION, event -> {
            page.setCenter(new ProjectsPage());
        });
        registerActionHandler("dashboard.about", ActionEvent.ACTION, event -> {
            page.setCenter(new AboutPage());
        });
    }

}
