package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToLongWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Long.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
