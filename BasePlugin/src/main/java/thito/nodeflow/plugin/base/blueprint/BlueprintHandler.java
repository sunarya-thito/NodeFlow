package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public class BlueprintHandler implements NodeCanvasHandler {
    private final BlueprintRegistry registry;
    private final BlueprintCanvas canvas;

    public BlueprintHandler(BlueprintRegistry registry, BlueprintCanvas canvas) {
        this.registry = registry;
        this.canvas = canvas;
    }

    public BlueprintRegistry getRegistry() {
        return registry;
    }

    public BlueprintCanvas getCanvas() {
        return canvas;
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
