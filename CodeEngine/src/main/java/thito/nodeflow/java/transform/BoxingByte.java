package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class BoxingByte implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(Byte.class) {
            private final Reference reference = getType().method("valueOf", Java.Class(byte.class)).invoke(source);
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
