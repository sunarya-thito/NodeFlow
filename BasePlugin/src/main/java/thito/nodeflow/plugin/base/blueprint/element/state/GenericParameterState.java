package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;

public class GenericParameterState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public String superType;
    public String[] interfaceTypes;
}
