package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.library.ui.layout.*;

public class TextAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object x) throws LayoutParserException {
        if (x instanceof Node) {
            Node component = (Node) x;
            switch (attribute) {
                case "value":
                case "text":
                    if (value != null && value.startsWith("${") && value.endsWith("}")) {
                        I18nItem item = I18n.$(value.substring(2, value.length() - 1));
                        if (component instanceof Labeled) {
                            ((Labeled) component).textProperty().bind(item.stringBinding());
                            return true;
                        } else if (component instanceof Text) {
                            ((Text) component).textProperty().bind(item.stringBinding());
                            return true;
                        }
                    } else {
                        if (component instanceof Labeled) {
                            ((Labeled) component).setText(value);
                            return true;
                        } else if (component instanceof Text) {
                            ((Text) component).setText(value);
                            return true;
                        }
                    }
                    break;
            }
        }
        return false;
    }
}
