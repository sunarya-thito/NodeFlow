package thito.nodeflow.internal.ui.dashboard.pages;

import com.sun.javafx.binding.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.util.*;

import java.util.*;

public class ProjectsPage extends DashboardPage {

    @Component("content")
    BorderPane content;

    @Component("search-field")
    TextField searchField;

    @Component("workspace-name")
    Label workspaceName;

    @Component("workspace-count")
    Label workspaceCount;

    private ChangeListener<Workspace> workspaceChangeListener;
    private ObservableList<ProjectProperties> projectList;
    private FilteredList<ProjectProperties> shownProjects;
    private SortedList<ProjectProperties> sortedProject;

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        projectList = FXCollections.observableArrayList();
        TaskThread.IO().schedule(() -> {
            // RESCAN PROJECT UPON PAGE SWITCH
            NodeFlow.getInstance().workspaceProperty().get().scanProjects();
        });
        workspaceChangeListener = (obs, old, val) -> {
            if (old != null) {
                Bindings.unbindContent(projectList, old.getProjectPropertiesList());
            }
            if (val != null) {
                Bindings.bindContent(projectList, val.getProjectPropertiesList());
                workspaceCount.textProperty().bind(I18n.direct("(%s)").format(Bindings.size(val.getProjectPropertiesList())));
            }
        };
        shownProjects = new FilteredList<>(projectList);
        sortedProject = new SortedList<>(shownProjects, (a, b) -> Long.compare(b.getLastModified(), a.getLastModified()));
        Bindings.bindContent(projectList, NodeFlow.getInstance().workspaceProperty().get().getProjectPropertiesList());
        NodeFlow.getInstance().workspaceProperty().addListener(workspaceChangeListener);
        registerActionHandler("dashboard.project-list.masonry-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectMasonrySkin(this));
        });
        registerActionHandler("dashboard.project-list.list-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectListSkin(this));
        });
    }

    public SortedList<ProjectProperties> getSortedProject() {
        return sortedProject;
    }

    @Override
    protected void onLayoutLoaded() {
        searchField.textProperty().addListener((obs, old, val) -> {
            Map<ProjectProperties, Double> scoreMap = new HashMap<>();
            if (val == null || val.isEmpty()) {
                shownProjects.predicateProperty().set(null);
                sortedProject.comparatorProperty().set((a, b) -> Long.compare(b.getLastModified(), a.getLastModified()));
            } else {
                shownProjects.predicateProperty().set(project -> scoreMap.computeIfAbsent(project, p ->
                        Toolkit.similarity(p.getName(), val) * 10 +
                                Toolkit.similarity(p.getDescription(), val) * 5 +
                                Toolkit.similarity(String.join(" ", p.getTags()), val) * 2 +
                                Toolkit.similarity(p.getDirectory().toFile().getAbsolutePath(), val)
                ) > 0);
                sortedProject.comparatorProperty().set(((Comparator<ProjectProperties>) (o1, o2) ->
                        Double.compare(scoreMap.getOrDefault(o2, 0d), scoreMap.getOrDefault(o1, 0d)))
                        .thenComparing((o1, o2) ->
                                Long.compare(o2.getLastModified(), o1.getLastModified())));
            }
        });
        workspaceName.textProperty().bind(MappedBinding.map(NodeFlow.getInstance().workspaceProperty(), workspace -> workspace.getRoot().getName()));
        Workspace workspace = NodeFlow.getInstance().workspaceProperty().get();
        if (workspace != null) {
            workspaceCount.textProperty().bind(I18n.direct("(%s)").format(Bindings.size(workspace.getProjectPropertiesList())));
        }
    }

}
