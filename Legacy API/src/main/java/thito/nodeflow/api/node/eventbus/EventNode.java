package thito.nodeflow.api.node.eventbus;

import thito.nodeflow.api.editor.node.*;

public interface EventNode extends Node {
    EventPriority getPriority();
    void setPriority(EventPriority priority);
}
