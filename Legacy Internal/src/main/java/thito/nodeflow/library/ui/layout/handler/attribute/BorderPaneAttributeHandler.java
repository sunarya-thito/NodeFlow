package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.layout.*;

public class BorderPaneAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object x) throws LayoutParserException {
        if (x instanceof Node) {
            Node component = (Node) x;
            switch (attribute) {
                case "borderpane.margin":
                    BorderPane.setMargin(component, LayoutHelper.parseInsets(value));
                    return true;
                case "borderpane.alignment":
                    BorderPane.setAlignment(component, LayoutHelper.parsePos(value));
                    return true;
                case "borderpane.position":
                    Node parent = component.getParent();
                    if (parent instanceof BorderPane) {
                        BorderPane borderPane = (BorderPane) parent;
                        // PATCH: Duplicate children
                        borderPane.getChildren().remove(component);
                        //
                        BorderPane_Position position = LayoutHelper.parseEnum(BorderPane_Position.class, value, true);
                        switch (position) {
                            case CENTER:
                                borderPane.setCenter(component);
                                break;
                            case TOP:
                                borderPane.setTop(component);
                                break;
                            case BOTTOM:
                                borderPane.setBottom(component);
                                break;
                            case LEFT:
                                borderPane.setLeft(component);
                                break;
                            case RIGHT:
                                borderPane.setRight(component);
                                break;
                        }
                    }
                    return true;
            }
        }
        return false;
    }

    public enum BorderPane_Position {
        CENTER, TOP, BOTTOM, LEFT, RIGHT;
    }
}
