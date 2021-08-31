package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;

import java.lang.reflect.*;

public class OutputParameterHandler extends JavaParameterHandler {
    public OutputParameterHandler(NodeParameter parameter) {
        super(parameter);
    }

    @Override
    public GenericStorage getGenericStorage() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public StringProperty displayNameProperty() {
        return null;
    }

    @Override
    public NodeParameter getParameter() {
        return null;
    }

    @Override
    public NodeParameterSkin createSkin() {
        return null;
    }

    @Override
    public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
        return false;
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
