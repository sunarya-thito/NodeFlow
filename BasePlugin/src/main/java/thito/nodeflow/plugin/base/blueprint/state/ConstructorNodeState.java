package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.plugin.base.blueprint.*;

import java.io.*;

public class ConstructorNodeState extends BlueprintNodeState {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConstructorNodeState(NodeProvider provider) {
        super(provider);
    }
}
