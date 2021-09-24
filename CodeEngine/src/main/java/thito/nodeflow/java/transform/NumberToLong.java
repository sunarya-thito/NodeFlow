package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class NumberToLong implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("longValue").invoke();
    }
}
