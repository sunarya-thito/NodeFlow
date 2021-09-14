package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class UnboxingCharacter implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(char.class) {
            private final Reference reference = source.method("charValue").invoke();

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
