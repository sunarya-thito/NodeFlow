package thito.nodeflow.plugin.base.blueprint_legacy.state;

import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;

import java.io.Serial;

public class EventStartNodeState extends BlueprintNodeState {
    @Serial
    private static final long serialVersionUID = 1L;

    public String[] parameterClassNames;

    public EventStartNodeState(NodeProvider provider) {
        super(provider);
    }

}
