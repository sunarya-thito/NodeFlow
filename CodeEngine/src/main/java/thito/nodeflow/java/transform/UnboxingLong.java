package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingLong implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(long.class) {
            private final Reference reference = source.method("longValue").invoke();

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
