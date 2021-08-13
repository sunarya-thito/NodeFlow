package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class LabelSkinHandler implements SkinHandler<Label> {
    @Override
    public void parse(SkinParser parser, Label node, Element element) {
        Element text = element.selectFirst("text");
        if (text != null) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
    }
}
