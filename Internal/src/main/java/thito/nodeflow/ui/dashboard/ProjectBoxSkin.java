package thito.nodeflow.ui.dashboard;

import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.binding.ActiveMappedListBinding;
import thito.nodeflow.binding.CombinedListBinding;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.project.Tag;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.ImagePane;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.ThemeManager;
import thito.nodeflow.util.Toolkit;

import java.util.*;

public class ProjectBoxSkin extends Skin {

    @Component("project-name")
    Labeled name;

    @Component("description")
    Labeled description;

    @Component("size")
    Labeled size;

    @Component("tags")
    Pane tags;

    @Component("project-icon")
    ImagePane icon;

    private ProjectProperties projectProperties;

    public ProjectBoxSkin(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("project.open", MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            DashboardWindow.getWindow().close();
            TaskThread.BG().schedule(() -> {
                // TODO open project
            });
        });
    }

    @Override
    protected void onLayoutLoaded() {
        icon.setImage(ProjectHelper.create(projectProperties.getName(), 150, 100));
        ThreadBinding.bind(size.textProperty(), Toolkit.formatFileSize(projectProperties.getDirectory().sizeProperty()),
                TaskThread.UI());
        name.textProperty().bind(projectProperties.nameProperty());
        description.textProperty().bind(projectProperties.descriptionProperty());

        /*
        ACTIVE HEAVY TAG FILTERING
         */
        ObservableList<Tag> combinedTags = FXCollections.observableArrayList();
        ObservableList<Tag> allTags = FXCollections.observableArrayList();
        FilteredList<Tag> nonNullTags = new FilteredList<>(allTags, Objects::nonNull);
        ObservableList<ObjectBinding<Tag>> dynamicTags = FXCollections.observableArrayList();
        ActiveMappedListBinding.bind(allTags, projectProperties.getTags(), tagId -> Bindings.createObjectBinding(() ->
                NodeFlow.getInstance().getTag(tagId), NodeFlow.getInstance().getTagMap()));
        MappedListBinding.bind(allTags, dynamicTags, ObjectBinding::get);

        Tag timeTag = new Tag(null);

        // Icon dynamic and depends on the Theme
        timeTag.iconProperty().bind(
                Bindings.createObjectBinding(() ->
                        new Image("rsrc:Themes/"+ ThemeManager.getInstance().getTheme().getName()+"/Icons/TimeIcon.png"),
                        ThemeManager.getInstance().themeProperty()));
        timeTag.nameProperty().bind(Toolkit.timeSinceFormat(TaskThread.UI().timeInMillisProperty().subtract(projectProperties.lastModifiedProperty())));

        CombinedListBinding.combine(combinedTags, FXCollections.singletonObservableList(timeTag), nonNullTags);
        MappedListBinding.bind(tags.getChildren(), combinedTags, TagSkin::new);
    }
}
