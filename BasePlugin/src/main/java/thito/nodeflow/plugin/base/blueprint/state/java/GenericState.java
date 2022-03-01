package thito.nodeflow.plugin.base.blueprint.state.java;

import java.io.Serial;
import java.io.Serializable;

public class GenericState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public TypeState extensionId;
    public TypeState[] implementationIds;
}
