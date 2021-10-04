package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.plugin.base.blueprint.NodeProvider;

import java.io.Serial;
import java.util.List;

public class EventStartNodeState extends BlueprintNodeState {
    @Serial
    private static final long serialVersionUID = 1L;

    public String[] parameterClassNames;

    public EventStartNodeState(NodeProvider provider) {
        super(provider);
    }

}
