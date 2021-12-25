package thito.nodeflow.ui.resource;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.batch.TaskQueue;
import thito.nodeflow.ui.dialog.Dialogs;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceExplorerView extends TreeView<Resource> {

    public static final Comparator<Resource> FILE_TYPE_COMPARATOR = Comparator.comparing(Resource::getType);
    public static final Comparator<Resource> FILE_NAME_COMPARATOR = Comparator.comparing(Resource::getName);

    private ObjectProperty<Comparator<Resource>> sortMode = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Resource>> filterMode = new SimpleObjectProperty<>();
    private TaskQueue taskQueue;

    public ResourceExplorerView() {
        setCellFactory(resourceTreeView -> new ResourceCell(this));
        setShowRoot(false);
        rootProperty().addListener((obs, old, val) -> {
            if (old instanceof ResourceItem) ((ResourceItem) old).view.set(null);
            if (val instanceof ResourceItem) ((ResourceItem) val).view.set(this);
        });
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                attemptDeleteSelectedFiles();
            }
        });
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void attemptDeleteSelectedFiles() {
        List<File> files = getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).filter(Objects::nonNull).map(Resource::toFile).collect(Collectors.toList());
        if (files.size() > 0) {
            Dialogs.askDeleteFile(files);
        }
    }

    public ObjectProperty<Comparator<Resource>> sortModeProperty() {
        return sortMode;
    }

    public ObjectProperty<Predicate<Resource>> filterModeProperty() {
        return filterMode;
    }
}
