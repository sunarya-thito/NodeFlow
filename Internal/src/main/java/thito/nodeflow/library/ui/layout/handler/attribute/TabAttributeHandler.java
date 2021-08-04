package thito.nodeflow.library.ui.layout.handler.attribute;

import com.jfoenix.controls.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.library.ui.layout.*;

public class TabAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof Tab) {
            Tab tab = (Tab) component;
            switch (attribute) {
                case "text":
                    if (value.startsWith("!{") && value.endsWith("}")) {
                        tab.textProperty().bind(I18n.$(value.substring(2, value.length()-1)).stringBinding());
                    } else {
                        tab.setText(value);
                    }
                    return true;
                case "closeable":
                    tab.setClosable(Boolean.parseBoolean(value));
                    return true;
            }
        }
        if (component instanceof JFXTabPane) {
            JFXTabPane pane = (JFXTabPane) component;
            switch (attribute) {
                case "side":
                    pane.setSide(LayoutHelper.parseEnum(Side.class, value, true));
                    return true;
            }
        }
        return false;
    }
}
