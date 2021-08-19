package thito.nodeflow.library.ui.resource;

import javafx.beans.property.*;
import javafx.scene.control.*;
import thito.nodeflow.library.resource.*;

import java.util.*;
import java.util.function.*;

public class ResourceExplorerView extends TreeView<Resource> {

    public static final Comparator<Resource> FILE_TYPE_COMPARATOR = Comparator.comparing(Resource::getType);
    public static final Comparator<Resource> FILE_NAME_COMPARATOR = Comparator.comparing(Resource::getName);

    private ObjectProperty<Comparator<Resource>> sortMode = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Resource>> filterMode = new SimpleObjectProperty<>();

    public ResourceExplorerView() {
        setCellFactory(resourceTreeView -> new ResourceCell(this));
        setShowRoot(false);
        rootProperty().addListener((obs, old, val) -> {
            if (old instanceof ResourceItem) ((ResourceItem) old).view.set(null);
            if (val instanceof ResourceItem) ((ResourceItem) val).view.set(this);
        });
    }

    public ObjectProperty<Comparator<Resource>> sortModeProperty() {
        return sortMode;
    }

    public ObjectProperty<Predicate<Resource>> filterModeProperty() {
        return filterMode;
    }
}
