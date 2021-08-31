package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;

import java.io.*;

public class BlueprintNodeState implements HandlerState {
    @Serial
    private static final long serialVersionUID = 1L;
    public String providerId;
    public NodeProvider provider;
}
