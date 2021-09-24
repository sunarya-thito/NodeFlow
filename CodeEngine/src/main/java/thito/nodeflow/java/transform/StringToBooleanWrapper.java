package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToBooleanWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Boolean.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
