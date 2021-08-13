package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class TextFieldHandler implements SkinHandler<TextField> {
    @Override
    public void parse(SkinParser parser, TextField node, Element element) {
        Element text = element.selectFirst("text");
        if (text != null) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
        Element placeholder = element.selectFirst("prompttext");
        if (placeholder != null) {
            StringProperty txt = new SimpleStringProperty(placeholder.ownText());
            node.promptTextProperty().bind(Language.getLanguage().replace(txt));
        }
    }
}
