package thito.nodeflow.library.ui.handler;

import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class ControlSkinHandler implements SkinHandler<Control> {
    @Override
    public void parse(SkinParser parser, Control node, Element element) {
        Element contextMenu = element.selectFirst("> contextmenu");
        if (contextMenu != null) {
            Menu menu = (Menu) MenuBarSkinHandler.createMenuItem(parser, contextMenu);
            ContextMenu m = new ContextMenu();
            List<MenuItem> items = new ArrayList<>(menu.getItems());
            menu.getItems().clear(); m.getItems().addAll(items);
            node.setContextMenu(m);
        }
    }
}
