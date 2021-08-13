package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.scene.text.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class TextSkinHandler implements SkinHandler<Text> {
    @Override
    public void parse(SkinParser parser, Text node, Element element) {
        Element text = element.selectFirst("text");
        if (text != null) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
    }
}
