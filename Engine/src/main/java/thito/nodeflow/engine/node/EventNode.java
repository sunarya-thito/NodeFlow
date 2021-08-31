package thito.nodeflow.engine.node;

import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

public class EventNode extends Node {

    public EventNode() {
        super();
    }

    public EventNode(NodeCanvas canvas, NodeState state) {
        super(canvas, state);
    }

    public void setHandler(EventNodeHandler handler) {
        super.setHandler(handler);
    }

    @Override
    protected EventNodeHandler createHandler(HandlerState state) {
        return getCanvas().getHandler().createEventHandler(this, state);
    }

    @Override
    public EventNodeSkin getSkin() {
        return (EventNodeSkin) super.getSkin();
    }
}
