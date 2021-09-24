package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToFloat implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Float.class).method("parseFloat", Java.Class(String.class)).invoke(source);
    }
}
