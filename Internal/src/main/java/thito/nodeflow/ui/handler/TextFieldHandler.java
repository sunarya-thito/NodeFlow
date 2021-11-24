package thito.nodeflow.ui.handler;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.language.Language;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class TextFieldHandler implements SkinHandler<TextField> {
    private static PseudoClass EMPTY = PseudoClass.getPseudoClass("empty");
    @Override
    public void parse(SkinParser parser, TextField node, Element element) {
        Element text = element.selectFirst("> text");
        if (text != null && text.hasText()) {
            StringProperty txt = new SimpleStringProperty(text.ownText());
            node.textProperty().bind(Language.getLanguage().replace(txt));
        }
        Element placeholder = element.selectFirst("> prompttext");
        if (placeholder != null) {
            StringProperty txt = new SimpleStringProperty(placeholder.ownText());
            node.promptTextProperty().bind(Language.getLanguage().replace(txt));
        }
        node.textProperty().addListener((obs, old, val) -> {
            node.pseudoClassStateChanged(EMPTY, val == null || val.isEmpty());
        });
        node.pseudoClassStateChanged(EMPTY, node.getText() == null || node.getText().isEmpty());
    }
}
