package thito.nodeflow.internal.ui.handler;

import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;

public class ButtonSkinHandler implements SkinHandler<Button> {
    @Override
    public void parse(SkinParser parser, Button node, Element element) {
        if (element.hasAttr("default")) {
            node.setDefaultButton(true);
        }
        if (element.hasAttr("cancel")) {
            node.setCancelButton(true);
        }
    }
}
