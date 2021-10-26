package thito.nodeflow.internal.ui.editor;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.form.CreateFileForm;
import thito.nodeflow.internal.ui.form.Form;
import thito.nodeflow.internal.ui.form.Validator;
import thito.nodeflow.internal.ui.resource.*;

import java.io.File;

public class EditorFilePanelSkin extends Skin {

    @Component("file-explorer")
    ResourceExplorerView explorerView;

    @Component("file-new")
    Menu newMenu;

    private EditorSkin editorSkin;

    public EditorFilePanelSkin(EditorSkin editorSkin) {
        this.editorSkin = editorSkin;
    }

    public ResourceExplorerView getExplorerView() {
        return explorerView;
    }

    @Override
    protected void onLayoutLoaded() {
        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setGraphic(new ImageView(module.getIconURL(ThemeManager.getInstance().getTheme())));
            menuItem.textProperty().bind(module.getDisplayName());
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                TreeItem<Resource> selected = explorerView.getSelectionModel().getSelectedItem();
                Resource root = selected != null ? selected.getValue() : null;
                if (root == null) root = editorSkin.getEditorWindow().getEditor().projectProperty().get().getSourceFolder();
                EditorSkin.showCreateFileForm(module, root);
            });
            newMenu.getItems().add(menuItem);
        }
        explorerView.sortModeProperty().set(ResourceExplorerView.FILE_TYPE_COMPARATOR.thenComparing(ResourceExplorerView.FILE_NAME_COMPARATOR));
        explorerView.rootProperty().bind(MappedBinding.map(editorSkin.getEditorWindow().getEditor().projectProperty(), project ->
                project == null ? null : new ResourceItem(project.getSourceFolder().getResourceManager().getResource(new File("C:\\Users\\Thito\\IdeaProjects\\NodeFlow Software\\target")))));
        explorerView.setCellFactory(view -> new ResourceCell(explorerView) {
            {
                itemProperty().addListener((obs, old, val) -> {
                    FileModule module = PluginManager.getPluginManager().getModule(val);
                    setGraphic(new ImageView(new Image(module.getIconURL(ThemeManager.getInstance().getTheme()))));
                });
                addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getClickCount() == 2) {
                        Resource resource = getItem();
                        if (resource != null && resource.getType() == ResourceType.FILE) {
                            editorSkin.getEditorWindow().getEditor().openFile(resource);
                        }
                    }
                });
            }
        });
    }
}
