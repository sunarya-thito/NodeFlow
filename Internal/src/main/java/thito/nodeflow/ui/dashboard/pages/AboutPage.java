package thito.nodeflow.ui.dashboard.pages;

import javafx.scene.control.*;
import thito.nodeflow.Version;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.dashboard.*;

public class AboutPage extends DashboardPage {

    @Component("about-header")
    Labeled header;

    @Component("about-content")
    Labeled content;

    @Override
    protected void onLayoutLoaded() {
        header.setText("NodeFlow "+ Version.getCurrentVersion().getVersion());
        content.setText(String.join("\n",
                "Contributors:",
                "Thito Yalasatria Sunarya"));
    }
}
