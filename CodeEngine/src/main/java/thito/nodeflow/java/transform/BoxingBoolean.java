package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class BoxingBoolean implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(Boolean.class) {
            private final Reference reference = getType().method("valueOf", Java.Class(boolean.class)).invoke(source);
            @Override
            public void writeByteCode() {
                reference.writeByteCode();
            }

            @Override
            public void writeSourceCode() {
                reference.writeSourceCode();
            }
        };
    }
}
