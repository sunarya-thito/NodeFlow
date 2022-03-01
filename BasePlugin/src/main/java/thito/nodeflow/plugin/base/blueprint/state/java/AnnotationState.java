package thito.nodeflow.plugin.base.blueprint.state.java;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AnnotationState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public TypeState id;
    public Map<String, Object> valueMap = new HashMap<>();
}
