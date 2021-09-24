package thito.nodeflow.internal.ui.handler;

import javafx.scene.Node;
import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.ScrollPane;

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
