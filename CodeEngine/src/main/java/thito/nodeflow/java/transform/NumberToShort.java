package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class NumberToShort implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("shortValue").invoke();
    }
}
