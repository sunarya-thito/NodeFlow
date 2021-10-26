package thito.nodeflow.plugin.base.blueprint.element.state;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ParameterState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String className;
    public String name;
}
