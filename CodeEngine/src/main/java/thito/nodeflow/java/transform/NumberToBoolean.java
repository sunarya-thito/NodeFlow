package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;
import thito.nodeflow.java.util.*;

public class NumberToBoolean implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Condition.Is(source).EqualTo(1);
    }
}
