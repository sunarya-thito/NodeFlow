package thito.nodeflow.ui.editor;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import thito.nodeflow.binding.MappedBinding;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.ThemeManager;
import thito.nodeflow.ui.resource.ResourceCell;
import thito.nodeflow.ui.resource.ResourceExplorerView;
import thito.nodeflow.ui.resource.ResourceItem;

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

    private EditorSkin editorSkin;

    public EditorFilePanelSkin(EditorSkin editorSkin) {
        this.editorSkin = editorSkin;
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

    @Override
    protected void onLayoutLoaded() {
        fileCut.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        fileCopy.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        fileDelete.disableProperty().bind(explorerView.getSelectionModel().selectedItemProperty().isNull());
        explorerView.disableProperty().bind(editorSkin.getEditorWindow().getEditor().projectProperty().isNull());
        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setGraphic(new ImageView(module.getIcon()));
            menuItem.textProperty().bind(module.getDisplayName());
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                TreeItem<Resource> selected = explorerView.getSelectionModel().getSelectedItem();
                Resource root = selected != null ? selected.getValue() : null;
                if (root == null) root = editorSkin.getEditorWindow().getEditor().projectProperty().get().getSourceFolder();
                EditorSkin.showCreateFileForm(editorSkin.getEditorWindow().getEditor().projectProperty().get(), module, root);
            });
            newMenu.getItems().add(menuItem);
        }
        explorerView.sortModeProperty().set(ResourceExplorerView.FILE_TYPE_COMPARATOR.thenComparing(ResourceExplorerView.FILE_NAME_COMPARATOR));
        explorerView.rootProperty().bind(MappedBinding.map(editorSkin.getEditorWindow().getEditor().projectProperty(), project ->
                project == null ? null : new ResourceItem(project.getSourceFolder())));
        explorerView.setCellFactory(view -> new ResourceCell(explorerView) {
            {
                itemProperty().addListener((obs, old, val) -> {
                    FileModule module = PluginManager.getPluginManager().getModule(val);
                    setGraphic(new ImageView(module.getIcon()));
                });
                addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getClickCount() == 2) {
                        Resource resource = getItem();
                        if (resource != null && resource.getType() == ResourceType.FILE) {
                            // TODO open file
                        }
                    }
                });
            }
        });
    }
}
