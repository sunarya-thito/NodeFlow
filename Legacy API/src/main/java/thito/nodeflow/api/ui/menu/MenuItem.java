package thito.nodeflow.api.ui.menu;

import javafx.beans.property.*;
import javafx.scene.control.Menu;
import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.action.Action;
import thito.nodeflow.api.locale.I18nItem;

import java.util.Arrays;
import java.util.List;

public interface MenuItem {
    static MenuItem create(Action action, MenuItemType type) {
        return NodeFlow.getApplication().getToolkit().createItem(action, type);
    }
    static MenuItem create(I18nItem label, Action action, MenuItemType type, MenuItem... child) {
        MenuItem item = create(action, type);
        item.getChildren().addAll(Arrays.asList(child));
        item.setLabel(label);
        return item;
    }

    static MenuItem create(I18nItem label) {
        return create(label, MenuItemType.BUTTON_TYPE);
    }

    static MenuItem create(String label) {
        return create(label, MenuItemType.BUTTON_TYPE);
    }

    static MenuItem create(I18nItem label, MenuItemType type, MenuItem... child) {
        MenuItem item = create(null, type);
        item.getChildren().addAll(Arrays.asList(child));
        item.setLabel(label);
        return item;
    }

    static MenuItem create(String label, Action action, MenuItemType type, MenuItem... child) {
        MenuItem item = create(action, type);
        item.getChildren().addAll(Arrays.asList(child));
        item.setLabel(label);
        return item;
    }

    static MenuItem create(String label, MenuItemType type, MenuItem... child) {
        MenuItem item = create(null, type);
        item.getChildren().addAll(Arrays.asList(child));
        item.setLabel(label);
        return item;
    }

    static MenuItem createSeparator() {
        return NodeFlow.getApplication().getToolkit().createSeparatorItem();
    }

    Action getAction();
    MenuItemHandler getHandler();
    List<MenuItem> getChildren();
    void setLabel(I18nItem label);
    void setLabel(String label);
    void dispatch();
    Menu impl_createRoot();
    javafx.scene.control.MenuItem impl_createPeer();
    BooleanProperty impl_disableProperty();
}
