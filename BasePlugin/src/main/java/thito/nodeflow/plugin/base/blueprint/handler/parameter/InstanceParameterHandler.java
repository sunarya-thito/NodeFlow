package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

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
        this.inputPort.colorProperty().bind(BlueprintManager.getBlueprintManager().getTypeColor(genericStorage, instance));
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
        return I18n.$("baseplugin.blueprint.instance-param");
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
