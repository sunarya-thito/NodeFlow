package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingFloat implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(float.class) {
            private final Reference reference = source.method("floatValue").invoke();

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
