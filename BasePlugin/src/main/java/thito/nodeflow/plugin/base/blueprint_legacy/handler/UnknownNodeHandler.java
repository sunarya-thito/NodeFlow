package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.*;

public class UnknownNodeHandler implements NodeHandler {
    private Node node;
    private HandlerState handlerState;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public UnknownNodeHandler(Node node, HandlerState handlerState) {
        this.handlerState = handlerState;
        this.node = node;
    }

    @Override
    public NodeSkin createSkin() {
        return new NodeSkin(getNode());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.unknown-node");
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public Node getNode() {
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
