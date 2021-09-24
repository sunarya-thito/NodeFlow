package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToBoolean implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Boolean.class).method("parseBoolean", Java.Class(String.class)).invoke(source);
    }
}
