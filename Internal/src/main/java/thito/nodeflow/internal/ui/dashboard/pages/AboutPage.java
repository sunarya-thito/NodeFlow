package thito.nodeflow.internal.ui.dashboard.pages;

import javafx.scene.control.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.internal.ui.*;

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
