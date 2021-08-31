package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public class UnknownNodeProvider implements NodeProvider {
    @Override
    public String getId() {
        return "?";
    }

    @Override
    public I18n displayNameProperty() {
        return null;
    }

    @Override
    public NodeHandler createHandler(Node node, BlueprintNodeState handlerState) {
        return null;
    }
}
