package thito.nodeflow.engine.node.handler;

import thito.nodeflow.engine.node.skin.*;

public interface EventNodeHandler extends NodeHandler {
    EventNodeSkin createSkin();
}
