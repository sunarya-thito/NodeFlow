package thito.nodeflow.ui.resource;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.*;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.TaskThread;

public class ResourceItem extends TreeItem<Resource> {
    ObjectProperty<ResourceExplorerView> view = new SimpleObjectProperty<>();
    private FilteredList<Resource> filteredList;
    private SortedList<Resource> sortedList;
    private ObservableSet<String> expandedPaths;
    public ResourceItem(Resource resource) {
        super(resource);
        filteredList = new FilteredList<>(resource.getChildren());
        sortedList = new SortedList<>(filteredList);
        ChangeListener<ResourceExplorerView> refreshListener = (obs, old, val) -> {
            filteredList.predicateProperty().unbind();
            sortedList.comparatorProperty().unbind();
            if (val != null) {
                filteredList.predicateProperty().bind(val.filterModeProperty());
                sortedList.comparatorProperty().bind(val.sortModeProperty());
            }
        };
        view.addListener(refreshListener);
        expandedProperty().addListener((obs, old, val) -> {
            if (expandedPaths != null) {
                if (val) {
                    expandedPaths.add(getValue().getPath());
                } else {
                    expandedPaths.remove(getValue().getPath());
                }
            }
        });
        ObservableList<TreeItem<Resource>> resourceItems = FXCollections.observableArrayList();
        ThreadBinding.bindContent(getChildren(), resourceItems, TaskThread.UI());
        MappedListBinding.bind(resourceItems, sortedList, ResourceItem::new);
        getChildren().addListener((ListChangeListener<TreeItem<Resource>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (TreeItem<Resource> child : change.getAddedSubList()) {
                        if (child instanceof ResourceItem) {
                            ((ResourceItem) child).setExpandedPaths(expandedPaths);
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

    public ObservableSet<String> getExpandedPaths() {
        return expandedPaths;
    }

    public void setExpandedPaths(ObservableSet<String> expandedPaths) {
        if (expandedPaths != null) {
            if (expandedPaths.contains(getValue().getPath())) {
                setExpanded(true);
            }
        }
        this.expandedPaths = expandedPaths;
        for (TreeItem<Resource> child : getChildren()) {
            if (child instanceof ResourceItem) {
                ((ResourceItem) child).setExpandedPaths(expandedPaths);
            }
        }
    }
}
