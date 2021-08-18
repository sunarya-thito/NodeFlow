package thito.nodeflow.library.ui.handler;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.ScrollPane;

public class ScrollPaneSkinHandler implements SkinHandler<ScrollPane> {
    @Override
    public void parse(SkinParser parser, ScrollPane node, Element element) {
        Element viewport = element.child(0);
        if (viewport != null) {
            Node n = parser.createNode(viewport);
            node.setContent(n);
            parser.handleNode(n, viewport);
        }
    }
}
