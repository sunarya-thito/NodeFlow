package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToShort implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Short.class).method("parseShort", Java.Class(String.class)).invoke(source);
    }
}
