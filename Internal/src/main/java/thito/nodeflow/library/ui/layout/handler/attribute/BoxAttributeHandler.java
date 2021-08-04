package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.layout.*;

public class BoxAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof Node) {
            switch (attribute) {
                case "hbox.hgrow":
                    HBox.setHgrow((Node) component, LayoutHelper.parseEnum(Priority.class, value, true));
                    return true;
                case "vbox.vgrow":
                    VBox.setVgrow((Node) component, LayoutHelper.parseEnum(Priority.class, value, true));
                    return true;
            }
        }
        return false;
    }
}
