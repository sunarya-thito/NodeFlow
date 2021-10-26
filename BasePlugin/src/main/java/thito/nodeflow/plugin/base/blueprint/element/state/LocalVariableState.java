package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class LocalVariableState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String typeName;
    public Object defaultValue;
    public boolean visible;
    public String name;
}
