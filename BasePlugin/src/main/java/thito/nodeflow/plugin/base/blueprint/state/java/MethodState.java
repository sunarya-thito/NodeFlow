package thito.nodeflow.plugin.base.blueprint.state.java;

import thito.nodeflow.plugin.base.blueprint.state.ProcedureState;

import java.io.Serial;

public class MethodState extends ProcedureState {
    @Serial
    private static final long serialVersionUID = 1L;
    public int modifiers;
    public GenericState[] genericStates;
    public TypeState returnType;
    public AnnotationState[] annotations;

    public JavaVariableState[] parameters() {
        return (JavaVariableState[]) parameters;
    }

    public JavaVariableState[] variables() {
        return (JavaVariableState[]) variables;
    }
}
