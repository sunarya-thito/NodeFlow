package thito.nodeflow.internal.ui.handler;

import javafx.scene.Node;
import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;

public class BorderPaneSkinHandler implements SkinHandler<BorderPane> {
    @Override
    public void parse(SkinParser parser, BorderPane node, Element element) {
        Element top = element.selectFirst("> top > *");
        Element bottom = element.selectFirst("> bottom > *");
        Element left = element.selectFirst("> left > *");
        Element right = element.selectFirst("> right > *");
        Element center = element.selectFirst("> center > *");
        if (top != null) {
            Node n = parser.createNode(top);
            node.setTop(n);
            parser.handleNode(n, top);
        }
        if (bottom != null) {
            Node n = parser.createNode(bottom);
            node.setBottom(n);
            parser.handleNode(n, bottom);
        }
        if (left != null) {
            Node n = parser.createNode(left);
            node.setLeft(n);
            parser.handleNode(n, left);
        }
        if (right != null) {
            Node n = parser.createNode(right);
            node.setRight(n);
            parser.handleNode(n, right);
        }
        if (center != null) {
            Node n = parser.createNode(center);
            node.setCenter(n);
            parser.handleNode(n, center);
        }
    }
}
