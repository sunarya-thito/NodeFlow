package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class StringToDouble implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(Double.class).method("parseDouble", Java.Class(String.class)).invoke(source);
    }
}
