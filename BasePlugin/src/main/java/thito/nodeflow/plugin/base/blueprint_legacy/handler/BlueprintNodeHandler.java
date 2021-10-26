package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;

public interface BlueprintNodeHandler extends NodeHandler {
    NodeProvider getProvider();
}
