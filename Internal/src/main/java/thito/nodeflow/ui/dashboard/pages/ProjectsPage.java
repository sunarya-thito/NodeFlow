package thito.nodeflow.ui.dashboard.pages;

import javafx.beans.binding.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.ReportedExceptionHandler;
import thito.nodeflow.binding.MappedBinding;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.project.Workspace;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.FormDialog;
import thito.nodeflow.ui.dashboard.*;
import thito.nodeflow.ui.form.internal.CreateProjectForm;
import thito.nodeflow.util.Toolkit;

import java.io.*;
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
                ThreadBinding.unbindContent(projectList, old.getProjectPropertiesList(), TaskThread.UI());
            }
            if (val != null) {
                ThreadBinding.bindContent(projectList, val.getProjectPropertiesList(), TaskThread.UI());
            }
        };
        shownProjects = new FilteredList<>(projectList);
        sortedProject = new SortedList<>(shownProjects, (a, b) -> Long.compare(b.getLastModified(), a.getLastModified()));
        ThreadBinding.bindContent(projectList, NodeFlow.getInstance().workspaceProperty().get().getProjectPropertiesList(), TaskThread.UI());
        NodeFlow.getInstance().workspaceProperty().addListener(workspaceChangeListener);
        registerActionHandler("dashboard.project-list.masonry-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectMasonrySkin(this));
        });
        registerActionHandler("dashboard.project-list.list-view", ActionEvent.ACTION, event -> {
            content.setCenter(new ProjectListSkin(this));
        });
        registerActionHandler("project.create", ActionEvent.ACTION, event -> {
            FormDialog.create(I18n.$("dashboard.forms.create-project"), new CreateProjectForm())
                    .show(form -> {
                        if (form == null) return;
                        TaskThread.IO().schedule(() -> {
                            try {
                                ProjectProperties projectProperties = NodeFlow.getInstance().workspaceProperty().get()
                                        .createProject(form.name.get(), form.folderName.get());
                                String description = form.description.get();
                                if (description != null && !description.trim().isEmpty()) {
                                    projectProperties.setDescription(description);
                                }
                            } catch (IOException e) {
                                ReportedExceptionHandler.report(e);
                            }
                        });
                    });
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
                        Toolkit.searchScore(p.getName(), val) * 10 +
                                Toolkit.searchScore(p.getDescription(), val) * 5 +
                                Toolkit.searchScore(String.join(" ", p.getTags()), val) * 2 +
                                Toolkit.searchScore(p.getDirectory().toFile().getAbsolutePath(), val)
                ) > 0);
                sortedProject.comparatorProperty().set(((Comparator<ProjectProperties>) (o1, o2) ->
                        Double.compare(scoreMap.getOrDefault(o2, 0d), scoreMap.getOrDefault(o1, 0d)))
                        .thenComparing((o1, o2) ->
                                Long.compare(o2.getLastModified(), o1.getLastModified())));
            }
        });
        workspaceName.textProperty().bind(MappedBinding.map(NodeFlow.getInstance().workspaceProperty(), workspace -> workspace.getRoot().getName()));
        workspaceCount.textProperty().bind(Bindings.createStringBinding(() -> "(" + projectList.size() + ")", projectList));
    }

}
