package thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.*;

import java.lang.reflect.*;

public class FieldValueParameterHandler extends JavaParameterHandler {
    private GenericStorage genericStorage;
    private Field field;
    private NodePort port;

    public FieldValueParameterHandler(GenericStorage genericStorage, Field field, NodeParameter parameter) {
        super(parameter);
        this.genericStorage = genericStorage;
        this.field = field;
        port = new NodePort(true, Color.BLACK, PortShape.CIRCLE);
        port.colorProperty().bind(BlueprintRegistry.getTypeColor(genericStorage, field.getGenericType()));
    }

    @Override
    public NodeParameterSkin createSkin() {
        return new NodeParameterSkin(getParameter());
    }

    @Override
    public HandlerState saveState() {
        return new FieldValueParameterHandlerState();
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
        return null;
    }

    @Override
    public NodePort getOutputPort() {
        return port;
    }
}
