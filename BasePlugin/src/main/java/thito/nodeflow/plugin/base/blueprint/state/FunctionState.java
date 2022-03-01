package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.engine.node.state.HandlerState;

import java.io.Serial;

public class FunctionState implements HandlerState {
    @Serial
    private static final long serialVersionUID = 1L;
    public String functionId;
}
