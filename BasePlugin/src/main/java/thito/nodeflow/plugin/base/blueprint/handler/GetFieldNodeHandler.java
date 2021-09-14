package thito.nodeflow.plugin.base.blueprint.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.*;
import thito.nodeflow.plugin.base.blueprint.state.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

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
        return I18n.$("baseplugin.blueprint.get-field-node").format(field.getName());
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
