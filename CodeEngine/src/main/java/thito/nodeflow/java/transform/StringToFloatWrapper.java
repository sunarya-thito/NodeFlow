package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToFloatWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Float.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
