package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;

public class InstanceParameterHandler extends ConstantHolderParameterHandler {
    private Type instance;
    private GenericStorage genericStorage;

    public InstanceParameterHandler(GenericStorage genericStorage, Type instance, NodeParameter parameter) {
        super(parameter);
        this.instance = instance;
        this.genericStorage = genericStorage;
    }

    @Override
    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public Type getType() {
        return instance;
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
    public NodePort getInputPort() {
        return null;
    }

    @Override
    public NodePort getOutputPort() {
        return null;
    }
}
