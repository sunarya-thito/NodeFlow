package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.layout.*;

public class AnchorPaneAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object x) throws LayoutParserException {
        if (x instanceof Node) {
            Node component = (Node) x;
            switch (attribute) {
                case "anchorpane.top":
                    AnchorPane.setTopAnchor(component, LayoutHelper.parseDouble(value));
                    return true;
                case "anchorpane.left":
                    AnchorPane.setLeftAnchor(component, LayoutHelper.parseDouble(value));
                    return true;
                case "anchorpane.right":
                    AnchorPane.setRightAnchor(component, LayoutHelper.parseDouble(value));
                    return true;
                case "anchorpane.bottom":
                    AnchorPane.setBottomAnchor(component, LayoutHelper.parseDouble(value));
                    return true;
            }
        }
        return false;
    }
}
