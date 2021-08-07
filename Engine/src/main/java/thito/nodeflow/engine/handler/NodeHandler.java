package thito.nodeflow.engine.handler;

import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeHandler {
    Node getNode();
    NodeSkin createSkin();
    HandlerState saveState();
}
