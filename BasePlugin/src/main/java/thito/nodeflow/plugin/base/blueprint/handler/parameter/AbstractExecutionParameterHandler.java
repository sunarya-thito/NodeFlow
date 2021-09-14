package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.library.language.*;

public abstract class AbstractExecutionParameterHandler implements NodeParameterHandler {

    private final NodeParameter nodeParameter;

    public AbstractExecutionParameterHandler(NodeParameter nodeParameter) {
        this.nodeParameter = nodeParameter;
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("baseplugin.blueprint.execution-param");
    }

    @Override
    public NodeParameter getParameter() {
        return nodeParameter;
    }

    @Override
    public NodeParameterSkin createSkin() {
        return new NodeParameterSkin(nodeParameter);
    }

    @Override
    public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
        NodeParameterHandler handler = parameter.getHandler();
        return handler instanceof AbstractExecutionParameterHandler;
    }

}
