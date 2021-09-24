package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class NumberToDouble implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("doubleValue").invoke();
    }
}
