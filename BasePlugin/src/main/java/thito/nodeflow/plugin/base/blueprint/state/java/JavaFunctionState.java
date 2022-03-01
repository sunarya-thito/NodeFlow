package thito.nodeflow.plugin.base.blueprint.state.java;

import thito.nodeflow.plugin.base.blueprint.state.FunctionState;

import java.io.Serial;

public class JavaFunctionState extends FunctionState {
    @Serial
    private static final long serialVersionUID = 1L;
    public GenericState[] genericStates;
}
