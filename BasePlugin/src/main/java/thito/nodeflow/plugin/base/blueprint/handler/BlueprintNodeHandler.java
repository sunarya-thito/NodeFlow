package thito.nodeflow.plugin.base.blueprint.handler;

import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.plugin.base.blueprint.NodeProvider;

public interface BlueprintNodeHandler extends NodeHandler {
    NodeProvider getProvider();
}
