package thito.nodeflow.engine.node.handler;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

public interface NodeParameterHandler {
    StringProperty displayNameProperty();
    NodeParameter getParameter();
    NodeParameterSkin createSkin();
    boolean acceptPairing(NodeParameter parameter, boolean asInput);
    NodePort getInputPort();
    NodePort getOutputPort();
    HandlerState saveState();

    default void onNodeLinked(NodeParameter other, boolean asInput) {}
    default void onNodeUnlinked(NodeParameter other, boolean asInput) {}
}
