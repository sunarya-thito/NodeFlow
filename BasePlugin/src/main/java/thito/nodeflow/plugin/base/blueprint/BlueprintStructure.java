package thito.nodeflow.plugin.base.blueprint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import thito.nodeflow.internal.project.module.FileStructure;

public class BlueprintStructure implements FileStructure {

    private ObjectProperty<Item> root = new SimpleObjectProperty<>(create());

    @Override
    public ObservableValue<Item> rootProperty() {
        return root;
    }

    protected Item create() {
        RootItem item = new RootItem();
        for (int i = 0; i < 5; i++) {
            VariableItem e = new VariableItem();
            item.getUnmodifiableChildren().add(e);
            e.name.set("Variable "+i);

        }
        for (int i = 0; i < 7; i++) {
            FunctionItem x = new FunctionItem();
            x.name.set("Procedure "+i);
            for (int j = 0; j < 3; j++) {
                VariableItem e = new VariableItem();
                x.getUnmodifiableChildren().add(e);
                e.name.set("Local Variable "+j);
            }
            for (int j = 0; j < 6; j++) {
                NodeItem e = new NodeItem();
                x.getUnmodifiableChildren().add(e);
                e.name.set("Node "+j);
            }
            item.getUnmodifiableChildren().add(x);
        }
        return item;
    }

    public abstract class AbstractItem implements Item {
        protected StringProperty name = new SimpleStringProperty();
        protected ObservableList<Item> items = FXCollections.observableArrayList();
        protected ContextMenu contextMenu = new ContextMenu();

        @Override
        public ObservableStringValue nameProperty() {
            return name;
        }

        @Override
        public ObservableList<Item> getUnmodifiableChildren() {
            return items;
        }

        @Override
        public ContextMenu getContextMenu() {
            return contextMenu;
        }
    }

    public class RootItem extends AbstractItem {
        @Override
        public String getIconURL() {
            return "theme://blueprint/Icons/BlueprintModuleIcon.png";
        }

        @Override
        public void dispatchFocus() {

        }

        @Override
        public void dispatchEditName(String name) {

        }

        @Override
        public void dispatchDelete() {

        }
    }

    public class FunctionItem extends AbstractItem {
        @Override
        public String getIconURL() {
            return "theme://blueprint/Icons/ProcedureIcon.png";
        }

        @Override
        public void dispatchFocus() {

        }

        @Override
        public void dispatchEditName(String name) {

        }

        @Override
        public void dispatchDelete() {

        }
    }

    public class VariableItem extends AbstractItem {
        @Override
        public String getIconURL() {
            return "theme://blueprint/Icons/VariableIcon.png";
        }

        @Override
        public void dispatchFocus() {

        }

        @Override
        public void dispatchEditName(String name) {

        }

        @Override
        public void dispatchDelete() {

        }
    }

    public class NodeItem extends AbstractItem {
        @Override
        public String getIconURL() {
            return "theme://blueprint/Icons/NodeIcon.png";
        }

        @Override
        public void dispatchFocus() {

        }

        @Override
        public void dispatchEditName(String name) {

        }

        @Override
        public void dispatchDelete() {

        }
    }

}
