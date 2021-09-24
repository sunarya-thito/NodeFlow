package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToByte implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Byte.class).method("parseByte", Java.Class(String.class)).invoke(source);
    }
}
