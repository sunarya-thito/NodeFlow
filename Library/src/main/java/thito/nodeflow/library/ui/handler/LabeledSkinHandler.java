package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class LabeledSkinHandler implements SkinHandler<Labeled> {
    @Override
    public void parse(SkinParser parser, Labeled node, Element element) {
        Element text = element.selectFirst("label");
        if (text != null && text.hasText()) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
        Element graphic = element.selectFirst("> graphic > *");
        if (graphic != null) {
            Node n = parser.createNode(graphic);
            node.setGraphic(n);
            parser.handleNode(n, graphic);
        }
        if (element.hasAttr("labeled.disableoverrun")) {
            node.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        }
    }
}
