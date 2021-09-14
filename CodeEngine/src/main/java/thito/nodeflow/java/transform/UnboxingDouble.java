package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingDouble implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(double.class) {
            private final Reference reference = source.method("doubleValue").invoke();

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
