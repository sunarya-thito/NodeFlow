package thito.nodeflow.internal.ui.menu;

import javafx.beans.property.*;
import javafx.scene.control.Menu;
import javafx.scene.control.*;
import thito.nodeflow.api.action.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;

import java.util.*;

public class MenuSeparatorImpl implements MenuItem {
    @Override
    public Action getAction() {
        return null;
    }

    @Override
    public MenuItemHandler getHandler() {
        return null;
    }

    @Override
    public List<MenuItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public BooleanProperty impl_disableProperty() {
        return null;
    }

    @Override
    public void setLabel(I18nItem label) {
    }

    @Override
    public void setLabel(String label) {
    }

    @Override
    public void dispatch() {
    }

    @Override
    public Menu impl_createRoot() {
        return null;
    }

    @Override
    public javafx.scene.control.MenuItem impl_createPeer() {
        return new SeparatorMenuItem();
    }
}
