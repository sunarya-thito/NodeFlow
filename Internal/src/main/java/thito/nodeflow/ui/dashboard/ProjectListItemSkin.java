package thito.nodeflow.ui.dashboard;

import javafx.beans.binding.*;
import javafx.beans.property.*;
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
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.project.Tag;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.ThemeManager;
import thito.nodeflow.util.Toolkit;

import java.util.*;

public class ProjectListItemSkin extends Skin {
    @Component("name")
    Label name;
    @Component("description")
    Label description;
    @Component("tags")
    FlowPane tags;

    private ProjectListSkin projectListSkin;
    private ProjectProperties projectProperties;

    public ProjectListItemSkin(ProjectListSkin projectListSkin, ProjectProperties projectProperties) {
        this.projectListSkin = projectListSkin;
        this.projectProperties = projectProperties;
    }

    public ProjectListSkin getProjectListSkin() {
        return projectListSkin;
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("project.open", MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            DashboardWindow.getWindow().openProject(projectProperties);
        });
    }

    @Override
    protected void onLayoutLoaded() {
        name.textProperty().bind(projectProperties.nameProperty());
        StringProperty sizeProp = new SimpleStringProperty();
        ThreadBinding.bind(sizeProp, Toolkit.formatFileSize(projectProperties.getDirectory().sizeProperty()),
                TaskThread.IO(),
                TaskThread.UI());
        description.textProperty().bind(projectProperties.descriptionProperty().concat(" (").concat(sizeProp).concat(")"));

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
