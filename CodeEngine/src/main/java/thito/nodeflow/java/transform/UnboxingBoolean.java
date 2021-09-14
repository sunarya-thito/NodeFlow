package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingBoolean implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(boolean.class) {
            private final Reference reference = source.method("booleanValue").invoke();

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
