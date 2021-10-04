package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.plugin.base.blueprint.NodeProvider;

import java.io.Serial;

public class EventGetFieldNodeHandlerState extends BlueprintNodeState {
    @Serial
    private static final long serialVersionUID = 1L;

    public EventGetFieldNodeHandlerState(NodeProvider provider) {
        super(provider);
    }
}
