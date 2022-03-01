package thito.nodeflow.plugin.base.blueprint.state.java;

import thito.nodeflow.plugin.base.blueprint.state.BlueprintState;

import java.io.Serial;

public class JavaBlueprintState extends BlueprintState {
    @Serial
    private static final long serialVersionUID = 1L;
    public int modifiers;
    public TypeState extensionId;
    public TypeState[] implementationIds;
    public GenericState[] genericStates;

    public JavaVariableState[] variables() { // a.k.a fields
        return (JavaVariableState[]) variables;
    }
}
