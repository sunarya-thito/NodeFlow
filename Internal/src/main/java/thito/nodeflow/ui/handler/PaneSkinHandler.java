package thito.nodeflow.ui.handler;

import javafx.scene.Node;
import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.ScrollPane;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class PaneSkinHandler implements SkinHandler<Pane> {
    @Override
    public void parse(SkinParser parser, Pane node, Element element) {
        if (node instanceof BorderPane || node instanceof ScrollPane) return;
        for (Element e : element.children()) {
            Node n = parser.createNode(e);
            node.getChildren().add(n);
            parser.handleNode(n, e);
        }
    }
}
