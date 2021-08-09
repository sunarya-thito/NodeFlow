package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.image.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

public class ImageViewAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object x) throws LayoutParserException {
        if (x instanceof Node) {
            Node component = (Node) x;
            switch (attribute) {
                case "value":
                    if (value.startsWith("!")) {
                        Icon icon = NodeFlow.getApplication().getResourceManager().getIcon(value.substring(1));
                        if (component instanceof ImageView) {
                            ((ImageView) component).imageProperty().bind(icon.impl_propertyPeer());
                        } else if (component instanceof BetterImageView) {
                            ((BetterImageView) component).imageProperty().bind(icon.impl_propertyPeer());
                        }
                    } else {
                        if (component instanceof ImageView) {
                            ((ImageView) component).setImage(NodeFlow.getApplication().getResourceManager().getImage(value).impl_propertyPeer().get());
                        } else if (component instanceof BetterImageView) {
                            ((BetterImageView) component).setImage(NodeFlow.getApplication().getResourceManager().getImage(value).impl_propertyPeer().get());
                        }
                    }
                    break;
            }
        }
        return false;
    }
}
