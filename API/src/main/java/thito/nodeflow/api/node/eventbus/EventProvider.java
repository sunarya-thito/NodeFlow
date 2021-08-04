package thito.nodeflow.api.node.eventbus;

import thito.nodeflow.api.editor.node.*;

import java.util.*;

public interface EventProvider extends NodeProvider {
    List<EventParameter> getEventParameters();
    default EventProvider addParameters(EventParameter... parameters) {
        for (EventParameter parameter : parameters) {
            getEventParameters().add(parameter);
        }
        return this;
    }
}
