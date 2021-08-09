package thito.nodeflow.internal.node.eventbus;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;

public class EventNodeImpl extends NodeImpl implements EventNode {
    private EventPriority priority;

    public EventNodeImpl(NodeModule module, ComponentState state, NodeProvider provider) {
        super(module, state, provider);
        if (state.getExtras().has("eventPriority")) {
            try {
                priority = EventPriority.valueOf(state.getExtras().getString("eventPriority"));
            } catch (Throwable t) {
            }
        }
        if (priority == null) {
            priority = EventPriority.NORMAL;
        }
    }

    @Override
    public EventPriority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(EventPriority priority) {
        this.priority = priority;
        getState().getExtras().set(priority.name(), "eventPriority");
    }
}
