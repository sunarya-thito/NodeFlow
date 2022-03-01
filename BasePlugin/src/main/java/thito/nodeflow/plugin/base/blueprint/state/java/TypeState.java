package thito.nodeflow.plugin.base.blueprint.state.java;

import java.io.Serial;
import java.io.Serializable;

public class TypeState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public AnnotationState[] annotations;
    public String id;
}
