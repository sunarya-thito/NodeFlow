package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;

public class InputParameterHandler extends ConstantHolderParameterHandler {
    private Parameter parameter;
    private NodePort inputPort;
    private GenericStorage genericStorage;

    public InputParameterHandler(GenericStorage genericStorage, Parameter parameter, NodeParameter nodeParameter) {
        super(nodeParameter);
        this.genericStorage = genericStorage;
        this.parameter = parameter;
        inputPort = new NodePort(false, BlueprintManager.getBlueprintManager().getTypeColor(parameter.getType()), PortShape.CIRCLE);
    }

    @Override
    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public Type getType() {
        return parameter.getParameterizedType();
    }

    @Override
    public StringProperty displayNameProperty() {
        return new SimpleStringProperty(parameter.getName());
    }

    @Override
    public NodePort getInputPort() {
        return inputPort;
    }

    @Override
    public NodePort getOutputPort() {
        return null;
    }

    @Override
    public ConstantHolderParameterHandlerState subSaveState() {
        ParameterHandlerState javaParameterHandlerState = new ParameterHandlerState();
        Parameter[] parameters = parameter.getDeclaringExecutable().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals(parameter)) {
                javaParameterHandlerState.parameterIndex = i;
                break;
            }
        }
        // is this even possible?
        if (javaParameterHandlerState.parameterIndex == -1) throw new IllegalArgumentException("invalid parameter index");
        return javaParameterHandlerState;
    }
}
