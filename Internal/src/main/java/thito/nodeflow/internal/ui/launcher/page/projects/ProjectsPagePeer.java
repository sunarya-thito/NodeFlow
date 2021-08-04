package thito.nodeflow.internal.ui.launcher.page.projects;

import com.jfoenix.controls.*;
import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.launcher.page.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.layout.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class ProjectsPagePeer extends UIContainer {

    @Component("search")
    private final ObjectProperty<JFXTextField> search = new SimpleObjectProperty<>();

    @Component("create-button")
    private final ObjectProperty<StackPane> createButton = new SimpleObjectProperty<>();

    @Component("project-list")
    private final ObjectProperty<VBox> projectList = new SimpleObjectProperty<>();

    @Component("props-name")
    private final ObjectProperty<Label> name = new SimpleObjectProperty<>();

    @Component("props-author")
    private final ObjectProperty<Label> author = new SimpleObjectProperty<>();

    @Component("props-time")
    private final ObjectProperty<Label> time = new SimpleObjectProperty<>();

    @Component("props-desc")
    private final ObjectProperty<Label> desc = new SimpleObjectProperty<>();

    @Component("props-project")
    private final ObjectProperty<BorderPane> props = new SimpleObjectProperty<>();

    @Component("props-overlay")
    private final ObjectProperty<VBox> overlay = new SimpleObjectProperty<>();

    @Component("delete-button")
    private final ObjectProperty<JFXButton> delete = new SimpleObjectProperty<>();

    @Component("edit-button")
    private final ObjectProperty<JFXButton> edit = new SimpleObjectProperty<>();

    @Component("open-button")
    private final ObjectProperty<JFXButton> open = new SimpleObjectProperty<>();

    @Component("props-thumbnail")
    private final ObjectProperty<ImageView> thumbnail = new SimpleObjectProperty<>();

    private SelectableGroup<ProjectItemPeer> selectableGroup;
    private ProjectsPageImpl page;
    private List<ProjectProperties> loaded;
    private ProjectItemPeer[] loadedItems;
    private ObservableList<ProjectItemPeer> visibleItems;

    public ProjectsPagePeer(ProjectsPageImpl page) {
        this.page = page;
        visibleItems = FXCollections.observableArrayList();
        setLayout(Layout.loadLayout("ProjectsPageUI"));
    }

    @Override
    protected void onLayoutReady() {
        selectableGroup = new SelectableGroup<>((ObservableList) projectList.get().getChildren(), ProjectItemPeer::selectedProperty);
        projectList.get().getChildren().addListener((InvalidationListener) observable -> {
            if (loaded != null && selectableGroup != null && selectableGroup.getSelected() != null && !loaded.contains(selectableGroup.getSelected().getProjectProperties())) {
                selectableGroup.setSelected(null);
            }
        });
        props.get().setVisible(false);
        overlay.get().setVisible(true);
        Image defTmb = thumbnail.get().getImage();
        selectableGroup.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                props.get().setVisible(true);
                overlay.get().setVisible(false);
                ProjectProperties projectProperties = val.getProjectProperties();
                name.get().setText(projectProperties.getName());
                author.get().setText(projectProperties.getAuthor());
                String dateModified = new SimpleDateFormat(I18n.$("project-properties-last-modified-format").getString(), I18n.impl_getLocalePeer()).format(new Date(projectProperties.getLastModified()));
                time.get().setText(dateModified);
                desc.get().setText(I18n.$("project-props-loading").getString());
                Resource thumb;
                if (!((thumb = projectProperties.getDirectory().getChild("thumbnail.png")) instanceof UnknownResource)) {
                    Task.runOnBackground("load-thumbnail", () -> {
                        try (InputStream inputStream = ((ResourceFile) thumb).openInput()) {
                            Image image = new Image(inputStream);
                            Task.runOnForeground("set-thumbnail", () -> {
                                thumbnail.get().setImage(image);
                            });
                        } catch (Throwable t) {
                            Toolkit.info("Failed to load thumbnail");
                            t.printStackTrace();
                        }
                    });
                } else {
                    thumbnail.get().setImage(defTmb);
                }
                Task.runOnBackground("calculate-project-size", () -> {
                    // do not use string binding, or should I?
                    long size = -1;
                    if (projectProperties.getDirectory() instanceof PhysicalResource) {
                        size = Toolkit.size(((PhysicalResource) projectProperties.getDirectory()).getSystemPath());
                    }
                    String facet = projectProperties.getFacet().getName();
                    String text = I18n.$("project-props-desc").getString(Toolkit.byteCountToDisplaySize(size), projectProperties.getDirectory().getPath(), facet);
                    Task.runOnForeground("refresh-project-description", () -> {
                        desc.get().setText(text);
                    });
                });
            } else {
                props.get().setVisible(false);
                overlay.get().setVisible(true);
            }
        });
        createButton.get().setOnMouseClicked(event -> {
            Dialogs.openCreateDialog(Toolkit.getWindow(this));
        });
        search.get().textProperty().addListener((obs, old, val) -> {
            if (val != null && val.isEmpty()) val = null;
            updateShown(val);
        });
        open.get().setOnAction(action -> {
            ProjectItemPeer peer = selectableGroup.getSelected();
            if (peer != null) {
                Task.runOnBackground("initialize-project", () -> {
                    ProjectProperties prop = peer.getProjectProperties();
                    ProjectFacet facet = prop.getFacet();
                    if (facet instanceof UnknownFacet) {
                        Task.runOnForeground("warn-project-facet", () -> {
                            Dialogs.inform(
                                    Toolkit.getWindow(this),
                                    I18n.$("project-error"),
                                    I18n.$("project-missing-facet").format(facet.getId()),
                                    thito.nodeflow.api.ui.dialog.Dialog.Type.INFO, Dialog.Level.DANGER, null);
                        });
                        return;
                    }
                    Project project = NodeFlow.getApplication().getProjectManager().getProject(prop);
                    if (project == null) {
                        project = NodeFlow.getApplication().getProjectManager().loadProject(prop);
                    }
                    final Project pr = project;
                    Task.runOnForeground("initialize-editor-window", () -> {
                        pr.getEditorWindow().show();
                    });
                });
            }
        });
        delete.get().setOnAction(action -> {
            ProjectItemPeer peer = selectableGroup.getSelected();
            if (peer != null) {
                Dialogs.deleteProject(Toolkit.getWindow(this), peer.getProjectProperties());
            }
        });
        edit.get().setOnAction(action -> {
            ProjectItemPeer peer = selectableGroup.getSelected();
            if (peer != null) {
                Dialogs.editProjectProperties(Toolkit.getWindow(this), peer.getProjectProperties());
            }
        });
        Bindings.bindContent(projectList.get().getChildren(), visibleItems);
    }

    @Override
    protected void initializeContent() {
        Toolkit.info("Initializing content");
        loaded = page.getShownProjects();
        Toolkit.info("Preparing "+loaded.size()+" projects...");
    }

    @Override
    protected void postLoadContent() {
        visibleItems.clear();
        loadedItems = loaded.stream().map(ProjectItemPeer::new).toArray(ProjectItemPeer[]::new);
        updateShown(null);
        super.postLoadContent();
    }

    public void updateShown() {
        updateShown(search.get().getText());
    }

    public  void updateShown(String filter) {
        if (filter != null) {
            visibleItems.removeIf(x -> calculateSearchScore(x.getProjectProperties(), filter) <= 0);
        }
        if (loaded != null) {
            for (ProjectItemPeer itemPeer : loadedItems) {
                if (!visibleItems.contains(itemPeer) && (filter == null || calculateSearchScore(itemPeer.getProjectProperties(), filter) > 0)) {
                    visibleItems.add(itemPeer);
                }
            }
        }
        if (filter != null) {
            visibleItems.sort((o1, o2) -> {
                long scoreA = 0;
                long scoreB = 0;
                if (filter != null) {
                    for (String x : filter.split("\\s+")) {
                        scoreA += calculateSearchScore(o1.getProjectProperties(), x);
                        scoreB += calculateSearchScore(o2.getProjectProperties(), x);
                    }
                }
                return Long.compare(scoreB + o2.getProjectProperties().getLastModified() - Toolkit.DATE_2020, scoreA + o1.getProjectProperties().getLastModified() - Toolkit.DATE_2020);
            });
        }
    }


    public long calculateSearchScore(ProjectProperties prop, String filter) {
        if (prop == null || filter == null) return Long.MAX_VALUE;
        String dateModified = new SimpleDateFormat(I18n.$("project-properties-last-modified-format").getString(), I18n.impl_getLocalePeer()).format(new Date(prop.getLastModified()));
        return calculateSearchScore(prop.getName(), filter) * 4 +
                calculateSearchScore(prop.getAuthor(), filter) * 3 +
                calculateSearchScore(prop.getDirectory().getPath(), filter) * 2 +
                calculateSearchScore(dateModified, filter);
    }

    public int calculateSearchScore(String propComparison, String filter) {
        int score = 0;
        if (propComparison.equals(filter)) {
            score += 1000;
        } else if (propComparison.contains(filter)) {
            score += 900;
        } else if (propComparison.equalsIgnoreCase(filter)) {
            score += 800;
        } else if (propComparison.toLowerCase().contains(filter.toLowerCase())) {
            score += 700;
        }
        if (filter.contains(propComparison)) {
            score += 500;
        } else if (filter.toLowerCase().contains(propComparison.toLowerCase())) {
            score += 250;
        }
        return score;
    }
}
