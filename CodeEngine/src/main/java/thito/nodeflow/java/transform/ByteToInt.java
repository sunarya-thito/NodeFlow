package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;

public class ByteToInt implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(int.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
            }

            @Override
            public void writeSourceCode() {
                source.writeSourceCode();
            }
        };
    }
}
