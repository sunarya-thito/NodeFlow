package thito.nodeflow.engine.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeHandler {
    StringProperty displayNameProperty();
    ObjectProperty<Image> iconProperty();
    Node getNode();
    NodeSkin createSkin();
    NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state);
    HandlerState saveState();
}
