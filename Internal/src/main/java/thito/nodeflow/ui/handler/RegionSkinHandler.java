package thito.nodeflow.ui.handler;

import javafx.scene.layout.*;
import javafx.scene.shape.*;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class RegionSkinHandler implements SkinHandler<Region> {
    @Override
    public void parse(SkinParser parser, Region node, Element element) {
        if (element.hasAttr("disableOverflow")) {
            Rectangle pane = new Rectangle();
            pane.widthProperty().bind(node.widthProperty());
            pane.heightProperty().bind(node.heightProperty());
            node.setClip(pane);
        }
    }
}
