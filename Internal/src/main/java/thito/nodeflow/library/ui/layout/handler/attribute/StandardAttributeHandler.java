package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.css.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.animation.*;
import thito.nodeflow.library.ui.layout.*;

public class StandardAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object x) throws LayoutParserException {
        if (x instanceof Labeled) {
            Labeled labeled = (Labeled) x;
            switch (attribute) {
                case "icon":
                    ResourceManager manager = NodeFlow.getApplication().getResourceManager();
                    ImageView view = new ImageView();
                    view.imageProperty().bind(value.startsWith("!") ? manager.getIcon(value.substring(1)).impl_propertyPeer() : manager.getImage(value).impl_propertyPeer());
                    labeled.setGraphic(view);
                    return true;
            }
        }
        if (x instanceof Parent) {
            switch (attribute) {
                case "parent.childrenchange":
                    ChildrenChange.hook((Parent) x, ChildrenChange.REGISTERED.get(value));
                    return true;
            }
        }
        if (x instanceof Control) {
            switch (attribute) {
                case "skin":
                    try {
                        ((Control) x).setSkin((Skin<?>) Class.forName(value).newInstance());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return true;
            }
        }
        if (x instanceof Node) {
            Node component = (Node) x;
            switch (attribute) {
                case "id":
                    component.setId(value);
                    return true;
                case "class":
                    component.getStyleClass().addAll(value.split(" "));
                    return true;
                case "disable-overflow":
                    if (component instanceof Region) {
                        Toolkit.clip((Region) component);
                    }
                    return true;
                case "pick-on-bounds":
                    component.setPickOnBounds(Boolean.parseBoolean(value));
                    return true;
                case "clip-arc":
                    if (x instanceof Region) {
                        Rectangle rectangle = new Rectangle();
                        rectangle.setArcWidth(LayoutHelper.parseDouble(value));
                        rectangle.setArcHeight(LayoutHelper.parseDouble(value));
                        rectangle.widthProperty().bind(((Region) x).widthProperty());
                        rectangle.heightProperty().bind(((Region) x).heightProperty());
                        ((Region) x).setClip(rectangle);
                    }
                    return true;
                case "selectable":
                    Pseudos.install(component, PseudoClass.getPseudoClass(value), MouseEvent.MOUSE_CLICKED, null);
                    return true;
                case "toggleable":
                    Pseudos.install(component, PseudoClass.getPseudoClass(value), MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_CLICKED);
                    return true;
                case "hoverable":
                    Pseudos.install(component, PseudoClass.getPseudoClass(value), MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED);
                    return true;
            }
        }
        if (x instanceof FlowPane) {
            switch (attribute) {
                case "flowpane.prefwraplength":
                    ((FlowPane) x).setPrefWrapLength(LayoutHelper.parseDouble(value));
                    return true;
            }
        }
        if (x instanceof TextField) {
            switch (attribute) {
                case "textfield.placeholder":
                    if (value.startsWith("${") && value.endsWith("}")) {
                        ((TextField) x).promptTextProperty().bind(I18n.$(value.substring(2, value.length()-1)).stringBinding());
                    } else {
                        ((TextField) x).setPromptText(value);
                    }
                    return true;
            }
        }
        return false;
    }

}
