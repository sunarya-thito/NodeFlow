package thito.nodeflow.ui.handler;

import javafx.beans.property.*;
import javafx.scene.text.*;
import org.jsoup.nodes.*;
import thito.nodeflow.language.Language;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class TextSkinHandler implements SkinHandler<Text> {
    @Override
    public void parse(SkinParser parser, Text node, Element text) {
        if (text != null && text.hasText()) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
    }
}
