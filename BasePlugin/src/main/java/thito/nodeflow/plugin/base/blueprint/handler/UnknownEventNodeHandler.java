package thito.nodeflow.plugin.base.blueprint.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.*;

public class UnknownEventNodeHandler implements EventNodeHandler {
    private EventNode node;
    private HandlerState handlerState;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public UnknownEventNodeHandler(EventNode node, HandlerState handlerState) {
        this.handlerState = handlerState;
        this.node = node;
    }

    @Override
    public EventNodeSkin createSkin() {
        return new EventNodeSkin(getNode());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("baseplugin.blueprint.unknown-node");
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public EventNode getNode() {
        return node;
    }

    @Override
    public NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state) {
        return new UnknownNodeParameterHandler(parameter, state);
    }

    @Override
    public HandlerState saveState() {
        return handlerState;
    }
}
