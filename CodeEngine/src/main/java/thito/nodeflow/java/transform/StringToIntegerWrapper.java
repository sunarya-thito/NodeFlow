package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToIntegerWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Integer.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
