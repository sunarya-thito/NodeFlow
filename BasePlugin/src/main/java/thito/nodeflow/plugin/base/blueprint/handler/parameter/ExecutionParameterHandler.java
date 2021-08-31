package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.library.language.*;

public class ExecutionParameterHandler implements NodeParameterHandler {

    private NodeParameter nodeParameter;

    public ExecutionParameterHandler(NodeParameter nodeParameter) {
        this.nodeParameter = nodeParameter;
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("baseplugin.blueprint.execution-param");
    }

    @Override
    public NodeParameter getParameter() {
        return null;
    }

    @Override
    public NodeParameterSkin createSkin() {
        return new NodeParameterSkin(nodeParameter);
    }

    @Override
    public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
        NodeParameterHandler handler = parameter.getHandler();
        return handler instanceof ExecutionParameterHandler;
    }

    @Override
    public NodePort getInputPort() {
        return null;
    }

    @Override
    public NodePort getOutputPort() {
        return null;
    }

    @Override
    public HandlerState saveState() {
        return null;
    }
}
