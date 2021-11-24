package thito.nodeflow.ui.resource;

import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;

public class ResourceCell extends TreeCell<Resource> {
    private ResourceExplorerView view;
    public ResourceCell(ResourceExplorerView view) {
        this.view = view;
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isEmpty()) {
                view.getSelectionModel().clearSelection();
            }
        });
    }

    public ResourceExplorerView getView() {
        return view;
    }

    @Override
    protected void updateItem(Resource resource, boolean b) {
        super.updateItem(resource, b);
        if (!b) {
            ResourceType type = resource.getType();
            if (type == ResourceType.DIRECTORY) {
                setGraphic(new ImageView("theme:Icons/Folder.png"));
            } else if (type == ResourceType.FILE) {
                FileModule module = PluginManager.getPluginManager().getModule(resource);
                if (module == null) {
                    setGraphic(new ImageView("theme:Icons/UnknownFile.png"));
                } else {
                    setGraphic(new ImageView(module.getIcon()));
                }
            } else {
                setGraphic(null);
            }
            setText(resource.getFileName());
        } else {
            setGraphic(null);
            setText(null);
        }
    }
}
