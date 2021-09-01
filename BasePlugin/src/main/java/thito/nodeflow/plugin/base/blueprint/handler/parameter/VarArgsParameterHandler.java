package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;

public class VarArgsParameterHandler extends ConstantHolderParameterHandler {

    private Parameter parameter;
    private NodeParameter nodeParameter;
    private Type type;

    public VarArgsParameterHandler(Parameter parameter, NodeParameter nodeParameter) {
        super(nodeParameter);
        this.parameter = parameter;
        this.nodeParameter = nodeParameter;
        Type type = parameter.getParameterizedType();
        if (type instanceof GenericArrayType) {
            this.type = ((GenericArrayType) type).getGenericComponentType();
        } else if (type instanceof Class && ((Class<?>) type).isArray()) {
            this.type = ((Class<?>) type).getComponentType();
        } else throw new UnsupportedOperationException(type.getTypeName());
    }

    @Override
    protected ConstantHolderParameterHandlerState subSaveState() {
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
    public GenericStorage getGenericStorage() {
        return null;
    }

    @Override
    public NodeParameterSkin createSkin() {
        return null;
    }

    @Override
    public Type getType() {
        return type;
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
}
