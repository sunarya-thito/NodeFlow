package thito.nodeflow.internal.ui.launcher;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.ui.launcher.*;
import thito.nodeflow.library.ui.layout.*;

public class LauncherPageContentUI extends UIContainer {

    @Component("viewport")
    private final ObjectProperty<BorderPane> viewport = new SimpleObjectProperty<>();

    private LauncherPage launcherPage;

    public LauncherPageContentUI(LauncherPage launcherPage) {
        this.launcherPage = launcherPage;
    }

    public BorderPane getViewport() {
        return viewport.get();
    }

    @Override
    protected void initializeContent() {
        if (launcherPage instanceof AbstractLauncherPage) {
            ((AbstractLauncherPage) launcherPage).loadContent();
        }
    }
}
