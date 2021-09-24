package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.internal.language.*;

public class UnknownNodeParameterHandler implements NodeParameterHandler {

    private NodeParameter parameter;
    private NodePort input, output;
    private HandlerState state;

    public UnknownNodeParameterHandler(NodeParameter parameter, HandlerState state) {
        this.parameter = parameter;
        this.state = state;
        this.input = new NodePort(true, Color.BLACK, PortShape.TRIANGLE);
        this.output = new NodePort(true, Color.BLACK, PortShape.TRIANGLE);
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.unknown-param");
    }

    @Override
    public NodeParameter getParameter() {
        return parameter;
    }

    @Override
    public NodeParameterSkin createSkin() {
        return new NodeParameterSkin(getParameter());
    }

    @Override
    public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
        return true;
    }

    @Override
    public NodePort getInputPort() {
        return input;
    }

    @Override
    public NodePort getOutputPort() {
        return output;
    }

    @Override
    public HandlerState saveState() {
        return state;
    }
}
