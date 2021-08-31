package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public interface EventNodeProvider extends NodeProvider {
    @Override
    EventNodeHandler createHandler(Node node, BlueprintNodeState handlerState);
}
