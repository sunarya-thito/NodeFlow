package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

import java.lang.reflect.*;

public class FieldInputValueParameterHandler extends ConstantHolderParameterHandler {
    private GenericStorage genericStorage;
    private Field field;
    private NodePort port;

    public FieldInputValueParameterHandler(GenericStorage genericStorage, Field field, NodeParameter parameter) {
        super(parameter);
        this.genericStorage = genericStorage;
        this.field = field;
        port = new NodePort(false, Color.BLACK, PortShape.CIRCLE);
        port.colorProperty().bind(BlueprintRegistry.getTypeColor(genericStorage, field.getGenericType()));
    }

    @Override
    protected ConstantHolderParameterHandlerState subSaveState() {
        return new FieldInputValueParameterHandlerState();
    }

    @Override
    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public Type getType() {
        return field.getGenericType();
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.field-value-param");
    }

    @Override
    public NodePort getInputPort() {
        return port;
    }

    @Override
    public NodePort getOutputPort() {
        return null;
    }
}
