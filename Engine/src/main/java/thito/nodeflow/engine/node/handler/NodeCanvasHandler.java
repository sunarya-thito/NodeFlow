package thito.nodeflow.engine.node.handler;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

public interface NodeCanvasHandler {
    NodeHandler createHandler(Node node, HandlerState state);
    NodeGroupSkin createGroupSkin(NodeGroup group);
    NodeCanvasSkin createCanvasSkin(NodeCanvas canvas);
}
