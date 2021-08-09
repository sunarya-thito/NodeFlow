package thito.nodeflow.engine.handler;

import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeCanvasHandler {
    NodeHandler createHandler(Node node, HandlerState state);
    NodeGroupSkin createGroupSkin(NodeGroup group);
    NodeCanvasSkin createCanvasSkin(NodeCanvas canvas);
}
