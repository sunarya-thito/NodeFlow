package thito.nodeflow.library.ui.handler;

import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

public class PaneSkinHandler implements SkinHandler<Pane> {
    @Override
    public void parse(SkinParser parser, Pane node, Element element) {
        if (node instanceof BorderPane) return;
        for (Element e : element.children()) {
            node.getChildren().add(parser.createNode(e));
        }
    }
}
