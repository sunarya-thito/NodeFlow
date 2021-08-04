package thito.nodeflow.internal.node.headless;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.state.*;

public class HeadlessEventNode extends HeadlessNode implements EventNode {
    private EventPriority priority;

    public HeadlessEventNode(NodeModule module, ComponentState state) {
        super(module, state);
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
