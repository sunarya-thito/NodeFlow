package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class NumberToFloat implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("floatValue").invoke();
    }
}
