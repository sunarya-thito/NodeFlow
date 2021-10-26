package thito.nodeflow.plugin.base.blueprint_legacy;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeCanvas;
import thito.nodeflow.engine.node.NodeGroup;
import thito.nodeflow.engine.node.handler.NodeCanvasHandler;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.engine.node.skin.NodeCanvasSkin;
import thito.nodeflow.engine.node.skin.NodeGroupSkin;
import thito.nodeflow.engine.node.state.HandlerState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;

public class BlueprintHandler implements NodeCanvasHandler {
    private final BlueprintRegistry registry;

    public BlueprintHandler(BlueprintRegistry registry) {
        this.registry = registry;
    }

    public BlueprintRegistry getRegistry() {
        return registry;
    }

    @Override
    public NodeHandler createHandler(Node node, HandlerState state) {
        NodeProvider nodeProvider = registry.getProviderById(((BlueprintNodeState) state).providerId);
        return nodeProvider.createHandler(this, node, (BlueprintNodeState) state);
    }

    @Override
    public NodeGroupSkin createGroupSkin(NodeGroup group) {
        return new NodeGroupSkin(group);
    }

    @Override
    public NodeCanvasSkin createCanvasSkin(NodeCanvas canvas) {
        return new NodeCanvasSkin(canvas);
    }
}
