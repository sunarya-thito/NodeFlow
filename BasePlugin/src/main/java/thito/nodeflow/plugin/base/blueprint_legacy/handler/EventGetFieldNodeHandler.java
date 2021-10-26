package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeParameterHandler;
import thito.nodeflow.engine.node.skin.NodeSkin;
import thito.nodeflow.engine.node.state.HandlerState;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.FieldValueParameterHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.InstanceParameterHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.EventGetFieldNodeHandlerState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.FieldValueParameterHandlerState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.InstanceParameterHandlerState;

import java.lang.reflect.Field;

public class EventGetFieldNodeHandler extends JavaNodeHandler {
    private Field field;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public EventGetFieldNodeHandler(Node node, NodeProvider provider, Field field) {
        super(node, provider);
        this.field = field;
    }

    @Override
    public BlueprintNodeState saveState() {
        return new EventGetFieldNodeHandlerState(getProvider());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.get-field-node").format(field.getName());
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
        if (state instanceof InstanceParameterHandlerState) {
            InstanceParameterHandler handler = new InstanceParameterHandler(getGenericStorage(), field.getDeclaringClass(), parameter);
            handler.setValue(((InstanceParameterHandlerState) state).value);
            return handler;
        }
        if (state instanceof FieldValueParameterHandlerState) {
            return new FieldValueParameterHandler(getGenericStorage(), field, parameter);
        }
        return null;
    }
}
