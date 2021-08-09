package thito.nodeflow.library.ui.layout.handler.attribute;

import com.jfoenix.controls.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

public class JFXAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof JFXMasonryPane) {
            switch (attribute) {
                case "jfxmasonrypane.layoutmode":
                    ((JFXMasonryPane) component).setLayoutMode(value.equalsIgnoreCase("MASONRY") ? JFXMasonryPane.LayoutMode.MASONRY : JFXMasonryPane.LayoutMode.BIN_PACKING);
                    return true;
            }
        }
        if (component instanceof BetterMasonryPane) {
            switch (attribute) {
                case "bettermasonrypane.layoutmode":
                    ((BetterMasonryPane) component).setLayoutMode(value.equals("MASONRY") ? BetterMasonryPane.LayoutMode.MASONRY : BetterMasonryPane.LayoutMode.BIN_PACKING);
                    return true;
            }
        }
        if (component instanceof JFXTextField) {
            switch (attribute) {
                case "jfxtextfield.prompttext":
                    if (value.startsWith("${") && value.endsWith("}")) {
                        ((JFXTextField) component).promptTextProperty().bind(I18n.$(value.substring(2, value.length()-1)).stringBinding());
                    } else {
                        ((JFXTextField) component).setPromptText(value);
                    }
                    return true;
            }
        }
        return false;
    }
}
