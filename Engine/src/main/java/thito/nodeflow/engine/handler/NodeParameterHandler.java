package thito.nodeflow.engine.handler;

import javafx.beans.property.*;
import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeParameterHandler {
    StringProperty displayNameProperty();
    NodeParameter getParameter();
    NodeParameterSkin createSkin();
    boolean acceptPairing(NodeParameter parameter, boolean asInput);
    NodePort getInputPort();
    NodePort getOutputPort();
    HandlerState saveState();
}
