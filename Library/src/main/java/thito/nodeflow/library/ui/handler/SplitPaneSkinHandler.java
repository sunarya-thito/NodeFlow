package thito.nodeflow.library.ui.handler;

import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

public class SplitPaneSkinHandler implements SkinHandler<SplitPane> {
    @Override
    public void parse(SkinParser parser, SplitPane node, Element element) {
        for (Element e : element.children()) {
            node.getItems().add(parser.createNode(e));
        }
    }
}
