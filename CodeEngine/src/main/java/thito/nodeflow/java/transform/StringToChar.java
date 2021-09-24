package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToChar implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return source.method("charAt", Java.Class(int.class)).invoke(0);
    }
}
