package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class NumberToByte implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("byteValue").invoke();
    }
}
