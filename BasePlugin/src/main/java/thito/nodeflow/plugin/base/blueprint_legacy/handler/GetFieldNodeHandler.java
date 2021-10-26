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

public class GetFieldNodeHandler extends JavaNodeHandler {
    private Field field;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public GetFieldNodeHandler(Node node, NodeProvider provider, Field field) {
        super(node, provider);
        this.field = field;
    }

    @Override
    public BlueprintNodeState saveState() {
        return new GetFieldNodeHandlerState(getProvider());
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
