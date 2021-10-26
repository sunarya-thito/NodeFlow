package thito.nodeflow.plugin.base.blueprint_legacy.state;

import thito.nodeflow.plugin.base.blueprint_legacy.*;

import java.io.*;

public class ConstructorNodeState extends BlueprintNodeState {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConstructorNodeState(NodeProvider provider) {
        super(provider);
    }
}
