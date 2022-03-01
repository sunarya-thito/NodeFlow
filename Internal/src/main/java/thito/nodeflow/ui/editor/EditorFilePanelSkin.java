package thito.nodeflow.ui.editor;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.docker.*;
import thito.nodeflow.ui.editor.docker.FileViewerComponent;
import thito.nodeflow.ui.resource.ResourceCell;
import thito.nodeflow.ui.resource.ResourceExplorerView;
import thito.nodeflow.ui.resource.ResourceItem;
import thito.nodeflow.util.Toolkit;

import java.util.Objects;

public class EditorFilePanelSkin extends Skin {

    @Component("file-explorer")
    ResourceExplorerView explorerView;

    @Component("file-new")
    Menu newMenu;

    @Component("file-import")
    MenuItem fileImport;
    @Component("file-cut")
    MenuItem fileCut;
    @Component("file-copy")
    MenuItem fileCopy;
    @Component("file-paste")
    MenuItem filePaste;
    @Component("file-delete")
    MenuItem fileDelete;
    @Component("file-rename")
    MenuItem fileRename;

    private ObservableSet<String> expandedPaths = FXCollections.observableSet();
    private ProjectContext projectContext;

    public EditorFilePanelSkin(ProjectContext projectContext) {
        this.projectContext = projectContext;
    }

    public ProjectContext getProjectContext() {
        return projectContext;
    }

    public ResourceExplorerView getExplorerView() {
        return explorerView;
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("editor.deleteSelectedFiles", ActionEvent.ACTION, event -> {
            explorerView.attemptDeleteSelectedFiles();
        });
    }

    private boolean selectedChange;
    @Override
    protected void onLayoutLoaded() {
        explorerView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileCut.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        fileCopy.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        fileDelete.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        fileRename.disableProperty().bind(Bindings.size(explorerView.getSelectionModel().getSelectedItems()).isEqualTo(1));
//        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
//            MenuItem menuItem = new MenuItem();
//            ImageView node = new ImageView();
//            node.imageProperty().bind(module.iconProperty());
//            menuItem.setGraphic(node);
//            menuItem.textProperty().bind(module.getDisplayName());
//            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
//                TreeItem<Resource> selected = explorerView.getSelectionModel().getSelectedItem();
//                Resource root = selected != null ? selected.getValue() : null;
//                if (root == null) {
//                    root = projectContext.getProject().getSourceFolder();
//                }
//                EditorSkin.showCreateFileForm(projectContext.getProject(), module, root);
//            });
//            newMenu.getItems().add(menuItem);
//        }
        explorerView.sortModeProperty().set(ResourceExplorerView.FILE_TYPE_COMPARATOR.thenComparing(ResourceExplorerView.FILE_NAME_COMPARATOR));

        ObservableSet<Resource> selectedFiles = projectContext.getSelectedFiles();
        explorerView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super TreeItem<Resource>>) c -> {
            if (selectedChange) return;
            selectedChange = true;
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().stream().map(TreeItem::getValue).forEach(selectedFiles::add);
                }
                if (c.wasRemoved()) {
                    c.getRemoved().stream().map(TreeItem::getValue).forEach(selectedFiles::remove);
                }
            }
            selectedChange = false;
        });
        selectedFiles.addListener((SetChangeListener<? super Resource>) c -> {
            if (selectedChange) return;
            selectedChange = true;
            if (c.wasAdded()) {
                TreeItem<Resource> resourceTreeItem = Toolkit.find(explorerView.getRoot(), c.getElementAdded());
                if (resourceTreeItem != null) {
                    explorerView.getSelectionModel().select(resourceTreeItem);
                }
            }
            if (c.wasRemoved()) {
                explorerView.getSelectionModel().getSelectedItems().stream().filter(x -> Objects.equals(x.getValue(), c.getElementRemoved())).findAny().ifPresent(x -> {
                    int index = explorerView.getRow(x);
                    if (index >= 0) {
                        explorerView.getSelectionModel().clearSelection(index);
                    }
                });
            }
            selectedChange = false;
        });

        ResourceItem treeItem = new ResourceItem(projectContext.getProject().getSourceFolder());
        treeItem.setExpandedPaths(expandedPaths);
        explorerView.setRoot(treeItem);

        explorerView.setCellFactory(view -> new ResourceCell(explorerView) {
            {
//                itemProperty().addListener((obs, old, val) -> {
//                    TaskThread.BG().schedule(() -> {
//                        FileModule module = PluginManager.getPluginManager().getModule(val);
//                        ImageView node = new ImageView();
//                        node.imageProperty().bind(module.iconProperty());
//                        TaskThread.UI().schedule(() -> {
//                            setGraphic(node);
//                        });
//                    });
//                });
                addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getClickCount() == 2) {
                        Resource resource = getItem();
                        if (resource != null && resource.getType() == ResourceType.FILE) {
                            for (EditorWindow editorWindow : projectContext.getActiveWindows()) {
                                DockerPane pane = editorWindow.getDockerPane();
                                for (DockerPosition position : DockerPosition.values()) {
                                    DockerTabPane tabPane = pane.getTabs(position);
                                    for (DockerTab tab : tabPane.getTabList()) {
                                        Node node = tab.contentProperty().get();
                                        if (node instanceof FileViewerComponent.Node view &&
                                            view.getResource().equals(resource)) {
                                            tabPane.focusedTabProperty().set(tab);
                                            return;
                                        }
                                    }
                                }
                            }
                            EditorWindow focusedWindow = projectContext.getFocusedWindow();
                            if (focusedWindow != null) {
                                DockerPane dockerPane = focusedWindow.getDockerPane();
                                FileViewerComponent fileViewerComponent = ProjectManager.getInstance().getFileViewerComponent();
                                DockNode dockNode = fileViewerComponent.createDockNode(focusedWindow.getContext(), resource);
                                DockerTab tab = new DockerTab(dockerPane.getContext(), dockNode);
                                DockerTabPane tabs = dockerPane.getTabs(fileViewerComponent.getDefaultPosition());
                                tabs.getTabList()
                                        .add(tab);
                                tabs.focusedTabProperty().set(tab);
                            }
                        }
                    }
                });
            }
        });
    }

    public ObservableSet<String> getExpandedPaths() {
        return expandedPaths;
    }
}
