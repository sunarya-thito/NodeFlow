package thito.nodeflow.internal.ui.resource;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.resource.*;

public class ResourceCell extends TreeCell<Resource> {
    public static final PseudoClass DIRECTORY = PseudoClass.getPseudoClass("directory");
    public static final PseudoClass FILE = PseudoClass.getPseudoClass("file");
    private ObjectProperty<ResourceType> resourceType = new SimpleObjectProperty<>();
    private ObjectProperty<String> extension = new SimpleObjectProperty<>();
    private ResourceExplorerView view;
    public ResourceCell(ResourceExplorerView view) {
        this.view = view;
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isEmpty()) {
                view.getSelectionModel().clearSelection();
            }
        });
        extension.addListener((obs, old, val) -> {
            if (old != null) {
                pseudoClassStateChanged(PseudoClass.getPseudoClass(old.toLowerCase()), false);
                getStyleClass().remove(old.toLowerCase());
            }
            if (val != null) {
                pseudoClassStateChanged(PseudoClass.getPseudoClass(val.toLowerCase()), true);
                getStyleClass().add(val.toLowerCase());
            }
        });
        resourceType.addListener((obs, old, val) -> {
            if (val == ResourceType.FILE) {
                pseudoClassStateChanged(FILE, true);
                pseudoClassStateChanged(DIRECTORY, false);
                getStyleClass().add("file");
                getStyleClass().remove("directory");
            } else {
                pseudoClassStateChanged(FILE, false);
                getStyleClass().remove("file");
                if (val == ResourceType.DIRECTORY) {
                    pseudoClassStateChanged(DIRECTORY, true);
                    getStyleClass().add("directory");
                } else {
                    pseudoClassStateChanged(DIRECTORY, false);
                    getStyleClass().remove("directory");
                }
            }
        });
    }

    @Override
    protected void updateItem(Resource resource, boolean b) {
        super.updateItem(resource, b);
        if (!b) {
            resourceType.bind(resource.typeProperty());
            extension.set(resource.getExtension());
            setText(resource.getFileName());
        } else {
            resourceType.unbind();
            resourceType.set(null);
            extension.set(null);
            setText(null);
        }
    }
}
