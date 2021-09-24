package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToShortWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Short.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
