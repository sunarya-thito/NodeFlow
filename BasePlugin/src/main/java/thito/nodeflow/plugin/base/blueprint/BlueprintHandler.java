package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public class BlueprintHandler implements NodeCanvasHandler {
    private final BlueprintManager manager = BlueprintManager.getBlueprintManager();

    @Override
    public NodeHandler createHandler(Node node, HandlerState state) {
        NodeProvider nodeProvider = manager.getNodeProviders().stream()
                .filter(provider -> provider.getId().equals(((BlueprintNodeState) state).providerId))
                .findAny().orElse(manager.getUnknownNodeProvider());
        return nodeProvider.createHandler(node, (BlueprintNodeState) state);
    }

    @Override
    public EventNodeHandler createEventHandler(Node node, HandlerState state) {
        EventNodeProvider eventNodeProvider = manager.getEventNodeProviders().stream()
                .filter(provider -> provider.getId().equals(((BlueprintNodeState) state).providerId))
                .findAny().orElse(manager.getUnknownEventNodeProvider());
        return eventNodeProvider.createHandler(node, (BlueprintNodeState) state);
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
