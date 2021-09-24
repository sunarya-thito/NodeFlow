package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToInt implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Integer.class).method("parseInt", Java.Class(String.class)).invoke(source);
    }
}
