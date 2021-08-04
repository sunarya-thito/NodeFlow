package thito.nodeflow.internal.ui.menu;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.Menu;
import thito.nodeflow.api.action.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.library.binding.*;

import java.util.*;

public class MenuItemImpl implements MenuItem {

    private Action action;
    private MenuItemHandler handler;
    private ObservableList<MenuItem> items = FXCollections.observableArrayList();
    private StringProperty text = new SimpleStringProperty();
    private BooleanProperty disable = new SimpleBooleanProperty();

    public MenuItemImpl(Action action, MenuItemType type) {
        this.action = action;
        this.handler = type.createHandler(this);
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public MenuItemHandler getHandler() {
        return handler;
    }

    @Override
    public void dispatch() {
        if (handler != null) {
            handler.handleDispatch();
        }
        if (action != null) {
            action.dispatch();
        }
    }

    @Override
    public void setLabel(String label) {
        text.set(label);
    }

    @Override
    public void setLabel(I18nItem label) {
        text.bind(label.stringBinding());
    }

    @Override
    public List<MenuItem> getChildren() {
        return items;
    }

    @Override
    public Menu impl_createRoot() {
        return createRoot();
    }

    @Override
    public javafx.scene.control.MenuItem impl_createPeer() {
        if (items.isEmpty()) {
            return create();
        } else {
            return createRoot();
        }
    }

    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    private javafx.scene.control.MenuItem create() {
        javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem();
        item.disableProperty().bind(disable);
        item.setOnAction(event -> {
            dispatch();
        });
        item.setGraphic(getHandler().impl_createPeer());
        item.textProperty().bind(text);
        return item;
    }

    private Menu createRoot() {
        Menu peer = new Menu();
        peer.disableProperty().bind(disable);
        peer.setOnAction(event -> dispatch());
        peer.setGraphic(getHandler().impl_createPeer());
        peer.textProperty().bind(text);
        MappedListBinding.bind(peer.getItems(), items, MenuItem::impl_createPeer);
        return peer;
    }
}
