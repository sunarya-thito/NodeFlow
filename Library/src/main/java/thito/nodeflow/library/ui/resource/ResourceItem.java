package thito.nodeflow.library.ui.resource;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.resource.*;

public class ResourceItem extends TreeItem<Resource> {
    ObjectProperty<ResourceExplorerView> view = new SimpleObjectProperty<>();
    private FilteredList<Resource> filteredList;
    private SortedList<Resource> sortedList;
    public ResourceItem(Resource resource) {
        super(resource);
        filteredList = new FilteredList<>(resource.getChildren());
        sortedList = new SortedList<>(filteredList);
        view.addListener((obs, old, val) -> {
            filteredList.predicateProperty().unbind();
            sortedList.comparatorProperty().unbind();
            if (val != null) {
                filteredList.predicateProperty().bind(val.filterModeProperty());
                sortedList.comparatorProperty().bind(val.sortModeProperty());
            }
        });
        MappedListBinding.bind(getChildren(), sortedList, ResourceItem::new);
        getChildren().addListener((ListChangeListener<TreeItem<Resource>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (TreeItem<Resource> child : change.getAddedSubList()) {
                        if (child instanceof ResourceItem) {
                            ((ResourceItem) child).view.bindBidirectional(view);
                        }
                    }
                }
                if (change.wasRemoved()) {
                    for (TreeItem<Resource> child : change.getRemoved()) {
                        if (child instanceof ResourceItem) {
                            ((ResourceItem) child).view.unbindBidirectional(view);
                        }
                    }
                }
            }
        });
    }
}
