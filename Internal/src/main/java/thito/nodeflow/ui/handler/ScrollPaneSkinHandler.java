package thito.nodeflow.ui.handler;

import javafx.scene.Node;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.ScrollPane;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

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
