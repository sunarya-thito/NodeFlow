package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class VariableState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String typeName;
    public String name;
    public Object defaultValue;
    public boolean visible;
}
