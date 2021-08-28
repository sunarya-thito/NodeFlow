package thito.nodeflow.internal.ui.dashboard;

import thito.nodeflow.internal.ui.dashboard.pages.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

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
