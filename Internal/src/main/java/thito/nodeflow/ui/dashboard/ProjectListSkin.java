package thito.nodeflow.ui.dashboard;

import javafx.scene.layout.*;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.dashboard.pages.*;

public class ProjectListSkin extends Skin {
    @Component("content-list")
    VBox content;

    private ProjectsPage page;

    public ProjectListSkin(ProjectsPage page) {
        this.page = page;
    }

    @Override
    protected void onLayoutLoaded() {
        MappedListBinding.bind(content.getChildren(), page.getSortedProject(), ProjectListItemSkin::new);
    }
}
