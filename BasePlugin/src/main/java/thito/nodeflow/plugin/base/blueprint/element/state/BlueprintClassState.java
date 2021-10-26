package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;

public class BlueprintClassState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public boolean isInterface;
    public String extensionTypeName;
    public String[] implementationTypeNames;
    public VariableState[] variables;
    public ProcedureState[] procedures;
}
