package thito.nodeflow.internal.ui.dashboard;

import thito.nodeflow.internal.ui.dashboard.pages.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.ui.*;

public class ProjectMasonrySkin extends Skin {
    @Component("pane")
    MasonryPane pane;

    private ProjectsPage page;

    public ProjectMasonrySkin(ProjectsPage page) {
        this.page = page;
    }

    @Override
    protected void onLayoutLoaded() {
        MappedListBinding.bind(pane.getChildren(), page.getSortedProject(), ProjectBoxSkin::new);
    }
}
