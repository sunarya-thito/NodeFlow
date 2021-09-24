package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToDoubleWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Double.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
