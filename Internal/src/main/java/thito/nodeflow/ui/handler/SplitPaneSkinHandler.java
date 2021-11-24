package thito.nodeflow.ui.handler;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class SplitPaneSkinHandler implements SkinHandler<SplitPane> {
    @Override
    public void parse(SkinParser parser, SplitPane node, Element element) {
        int index = 0;
        for (Element e : element.children()) {
            Node n = parser.createNode(e);
            if (e.hasAttr("splitpane.dividerposition")) {
                node.setDividerPosition(index, Double.parseDouble(e.attr("splitpane.dividerposition")));
            }
            node.getItems().add(n);
            parser.handleNode(n, e);
            index++;
        }
    }
}
