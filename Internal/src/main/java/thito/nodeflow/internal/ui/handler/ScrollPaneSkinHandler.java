package thito.nodeflow.internal.ui.handler;

import javafx.scene.Node;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.ScrollPane;

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
