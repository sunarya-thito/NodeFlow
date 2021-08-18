package thito.nodeflow.internal.ui.dashboard.pages;

import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.util.*;

import java.util.*;

public class ProjectsPage extends DashboardPage {

    @Component("content")
    BorderPane content;

    @Component("search-field")
    TextField searchField;

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("dashboard.project-list.masonry-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectMasonrySkin());
        });
        registerActionHandler("dashboard.project-list.list-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectListSkin());
        });
    }

    @Override
    protected void onLayoutLoaded() {
        searchField.textProperty().addListener((obs, old, val) -> {
        });
    }

}
