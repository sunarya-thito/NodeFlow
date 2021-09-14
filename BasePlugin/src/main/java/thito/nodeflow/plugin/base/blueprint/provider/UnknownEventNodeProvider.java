package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public class UnknownEventNodeProvider implements EventNodeProvider {

    @Override
    public String getId() {
        return "??";
    }

    @Override
    public EventNodeHandler createHandler(Node node, BlueprintNodeState handlerState) {
        return null;
    }

    @Override
    public EventNode createNode() {
        return null;
    }

    @Override
    public I18n displayNameProperty() {
        return null;
    }
}
