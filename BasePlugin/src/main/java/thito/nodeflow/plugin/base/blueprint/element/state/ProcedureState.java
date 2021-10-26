package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ProcedureState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public ParameterState[] inputs;
    public LocalVariableState[] variables;
    public String name;
}
