package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingInteger implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(int.class) {
            private final Reference reference = source.method("intValue").invoke();

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
