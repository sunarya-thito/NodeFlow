package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class ObjectToString implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return Java.Class(String.class).method("valueOf", BCHelper.isPrimitive(source.getType()) ? source.getType() :
                Java.Class(Object.class)).invoke(source);
    }
}
