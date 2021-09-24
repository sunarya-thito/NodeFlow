package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;
import thito.nodeflow.java.util.*;

public class ObjectToBoolean implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Condition.Is(source).NotNull();
    }
}
