package thito.nodeflow.plugin.base.blueprint.state.java;

import thito.nodeflow.plugin.base.blueprint.state.VariableState;

import java.io.Serial;

public class JavaVariableState extends VariableState {
    @Serial
    private static final long serialVersionUID = 1L;
    public int modifiers;
    public TypeState type;
}
