package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.engine.node.state.HandlerState;

import java.io.Serial;

public class ProcedureState implements HandlerState {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public VariableState[] parameters;
    public VariableState[] variables;
}
