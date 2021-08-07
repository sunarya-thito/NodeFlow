package thito.nodeflow.engine.handler;

import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeParameterHandler {
    NodeParameter getParameter();
    NodeParameterSkin createSkin();
    NodePort createPort();
    boolean acceptPairing(NodeParameter parameter, boolean asInput);
    HandlerState saveState();
}
