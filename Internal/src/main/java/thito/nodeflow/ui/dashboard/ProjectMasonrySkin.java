package thito.nodeflow.ui.dashboard;

import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.MasonryPane;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.dashboard.pages.*;

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
