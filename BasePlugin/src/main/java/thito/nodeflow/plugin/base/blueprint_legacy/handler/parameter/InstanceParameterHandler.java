package thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.*;

import java.lang.reflect.*;

public class InstanceParameterHandler extends ConstantHolderParameterHandler {
    private Type instance;
    private GenericStorage genericStorage;
    private NodePort inputPort;

    public InstanceParameterHandler(GenericStorage genericStorage, Type instance, NodeParameter parameter) {
        super(parameter);
        this.instance = instance;
        this.genericStorage = genericStorage;
        this.inputPort = new NodePort(false, Color.BLACK, PortShape.CIRCLE);
        this.inputPort.colorProperty().bind(BlueprintRegistry.getTypeColor(genericStorage, instance));
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
        return new InstanceParameterHandlerState();
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.instance-param");
    }

    @Override
    public NodePort getInputPort() {
        return inputPort;
    }

    @Override
    public NodePort getOutputPort() {
        return null;
    }
}
