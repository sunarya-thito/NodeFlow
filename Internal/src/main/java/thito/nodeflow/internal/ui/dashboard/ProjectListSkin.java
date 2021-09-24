package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.layout.*;
import thito.nodeflow.internal.ui.dashboard.pages.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.ui.*;

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
