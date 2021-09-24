package thito.nodeflow.internal.ui.editor;

import javafx.scene.image.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.resource.*;

public class EditorFilePanelSkin extends Skin {

    @Component("file-explorer")
    ResourceExplorerView explorerView;

    private EditorSkin editorSkin;

    public EditorFilePanelSkin(EditorSkin editorSkin) {
        this.editorSkin = editorSkin;
    }

    @Override
    protected void onLayoutLoaded() {
        explorerView.sortModeProperty().set(ResourceExplorerView.FILE_TYPE_COMPARATOR.thenComparing(ResourceExplorerView.FILE_NAME_COMPARATOR));
        explorerView.rootProperty().bind(MappedBinding.map(editorSkin.getEditorWindow().getEditor().projectProperty(), project ->
                project == null ? null : new ResourceItem(project.getSourceFolder())));
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
