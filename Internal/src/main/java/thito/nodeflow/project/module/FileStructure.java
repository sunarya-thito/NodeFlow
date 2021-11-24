package thito.nodeflow.project.module;

import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;

public interface FileStructure {

    ObservableValue<Item> rootProperty();

    interface Item {
        ObservableStringValue nameProperty();
        String getIconURL();
        ObservableList<Item> getUnmodifiableChildren();
        void dispatchFocus();
        void dispatchEditName(String name);
        void dispatchDelete();
        ContextMenu getContextMenu();
    }
}
