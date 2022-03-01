package thito.nodeflow.plugin.base.blueprint.state;

import java.io.Serial;
import java.io.Serializable;

public class VariableState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public String typeId;
}
