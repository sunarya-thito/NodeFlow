package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToByteWrapper implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Byte.class).method("valueOf", Java.Class(String.class)).invoke(source);
    }
}
