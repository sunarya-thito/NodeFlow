package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingByte implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(byte.class) {
            private final Reference reference = source.method("byteValue").invoke();

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
