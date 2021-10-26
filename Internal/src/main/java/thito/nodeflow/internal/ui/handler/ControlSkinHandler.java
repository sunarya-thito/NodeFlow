package thito.nodeflow.internal.ui.handler;

import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;

public class ControlSkinHandler implements SkinHandler<Control> {
    @Override
    public void parse(SkinParser parser, Control node, Element element) {
        Element contextMenu = element.selectFirst("> contextmenu");
        if (contextMenu != null) {
            ContextMenu m = new ContextMenu();
            for (Element child : contextMenu.children()) {
                MenuItem menuItem = MenuBarSkinHandler.createMenuItem(parser, child);
                parser.handleMenu(menuItem, child);
                m.getItems().add(menuItem);
            }
            node.setContextMenu(m);
        }
    }
}
