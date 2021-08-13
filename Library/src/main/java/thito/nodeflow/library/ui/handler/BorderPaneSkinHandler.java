package thito.nodeflow.library.ui.handler;

import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

public class BorderPaneSkinHandler implements SkinHandler<BorderPane> {
    @Override
    public void parse(SkinParser parser, BorderPane node, Element element) {
        Element top = element.selectFirst("top > *");
        Element bottom = element.selectFirst("bottom > *");
        Element left = element.selectFirst("left > *");
        Element right = element.selectFirst("right > *");
        Element center = element.selectFirst("center > *");
        if (top != null) {
            node.setTop(parser.createNode(top));
        }
        if (bottom != null) {
            node.setBottom(parser.createNode(bottom));
        }
        if (left != null) {
            node.setLeft(parser.createNode(left));
        }
        if (right != null) {
            node.setRight(parser.createNode(right));
        }
        if (center != null) {
            node.setCenter(parser.createNode(center));
        }
    }
}
