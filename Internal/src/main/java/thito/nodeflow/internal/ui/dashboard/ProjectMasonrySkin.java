package thito.nodeflow.internal.ui.dashboard;

import thito.nodeflow.library.ui.*;

public class ProjectMasonrySkin extends Skin {
    @Component("pane")
    MasonryPane pane;

    @Override
    protected void onLayoutLoaded() {
        for (int i = 0; i < 10; i++) {
            ProjectBoxSkin e = new ProjectBoxSkin();
            pane.getChildren().add(e);
        }
    }
}
