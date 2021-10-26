package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.*;

import java.lang.reflect.*;

public class SetFieldNodeHandler extends JavaNodeHandler {
    private Field field;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public SetFieldNodeHandler(Node node, NodeProvider provider, Field field) {
        super(node, provider);
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public BlueprintNodeState saveState() {
        return new SetFieldNodeHandlerState(getProvider());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.set-field-node");
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public NodeSkin createSkin() {
        return new NodeSkin(getNode());
    }

    @Override
    public NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state) {
        if (state instanceof ExecutionParameterHandlerState) {
            return new ExecutionParameterHandler(parameter);
        }
        if (state instanceof InstanceParameterHandlerState) {
            InstanceParameterHandler handler = new InstanceParameterHandler(getGenericStorage(), getField().getDeclaringClass(), parameter);
            handler.setValue(((InstanceParameterHandlerState) state).value);
            return handler;
        }
        if (state instanceof FieldInputValueParameterHandlerState) {
            FieldInputValueParameterHandler handler = new FieldInputValueParameterHandler(getGenericStorage(), getField(), parameter);
            handler.setValue(((FieldInputValueParameterHandlerState) state).value);
            return handler;
        }
        return null;
    }
}
