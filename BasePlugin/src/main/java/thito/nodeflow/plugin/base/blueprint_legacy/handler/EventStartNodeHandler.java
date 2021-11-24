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
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.ExecutionParameterHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.EventStartNodeState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.parameter.ExecutionParameterHandlerState;

public class EventStartNodeHandler extends JavaNodeHandler {
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    public EventStartNodeHandler(Node node, NodeProvider provider) {
        super(node, provider);
    }

    @Override
    public BlueprintNodeState saveState() {
        return new EventStartNodeState(getProvider());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.event-start-node");
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
        return null;
    }
}
