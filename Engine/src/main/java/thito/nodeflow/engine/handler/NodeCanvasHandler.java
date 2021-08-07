package thito.nodeflow.engine.handler;

import thito.nodeflow.engine.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

public interface NodeCanvasHandler {
    NodeHandler createHandler(Node node, HandlerState state);
    NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state);
    NodeGroupSkin createGroupSkin();
    NodeCanvasSkin createCanvasSkin();
}
