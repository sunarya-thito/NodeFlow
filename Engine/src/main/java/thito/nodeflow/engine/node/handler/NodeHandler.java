package thito.nodeflow.engine.node.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

public interface NodeHandler {
    StringProperty displayNameProperty();
    ObjectProperty<Image> iconProperty();
    Node getNode();
    NodeSkin createSkin();
    NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state);
    HandlerState saveState();
}
