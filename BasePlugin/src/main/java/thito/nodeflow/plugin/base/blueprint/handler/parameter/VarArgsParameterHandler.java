package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

import java.lang.reflect.*;

public class VarArgsParameterHandler extends ConstantHolderParameterHandler {

    private Parameter parameter;
    private Type type;
    private NodePort inputPort;
    private GenericStorage genericStorage;

    public VarArgsParameterHandler(GenericStorage genericStorage, Parameter parameter, NodeParameter nodeParameter) {
        super(nodeParameter);
        this.parameter = parameter;
        this.genericStorage = genericStorage;
        Type type = parameter.getParameterizedType();
        if (type instanceof GenericArrayType) {
            this.type = ((GenericArrayType) type).getGenericComponentType();
        } else if (type instanceof Class && ((Class<?>) type).isArray()) {
            this.type = ((Class<?>) type).getComponentType();
        } else throw new UnsupportedOperationException(type.getTypeName());
        this.inputPort = new NodePort(false, Color.BLACK, PortShape.CIRCLE);
        this.inputPort.colorProperty().bind(BlueprintRegistry.getTypeColor(genericStorage, type));
    }


    @Override
    protected ConstantHolderParameterHandlerState subSaveState() {
        VarArgsParameterHandlerState state = new VarArgsParameterHandlerState();
        Parameter[] parameters = parameter.getDeclaringExecutable().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals(parameter)) {
                state.parameterIndex = i;
                break;
            }
        }
        if (state.parameterIndex == -1) throw new IllegalArgumentException("invalid parameter index");
        return state;
    }

    @Override
    public StringProperty displayNameProperty() {
        return new SimpleStringProperty(parameter.getName());
    }

    @Override
    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public Type getType() {
        return type;
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
