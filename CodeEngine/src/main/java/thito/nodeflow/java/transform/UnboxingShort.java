package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingShort implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(short.class) {
            private final Reference reference = source.method("shortValue").invoke();

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
