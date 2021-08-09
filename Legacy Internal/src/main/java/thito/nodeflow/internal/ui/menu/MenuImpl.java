package thito.nodeflow.internal.ui.menu;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.ui.menu.Menu;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.binding.*;

import java.util.*;

public class MenuImpl implements Menu {
    private ObservableList<MenuItem> items = FXCollections.observableArrayList();

    @Override
    public List<MenuItem> getItems() {
        return items;
    }

    @Override
    public Node impl_createPeer() {
        MenuBar bar = new MenuBar();
        Toolkit.style(bar, "menu-bar");
        MappedListBinding.bind(bar.getMenus(), items, MenuItem::impl_createRoot);
        return bar;
    }
}
