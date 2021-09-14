package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class ShortToLong implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(long.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.I2L));
            }

            @Override
            public void writeSourceCode() {
                source.writeSourceCode();
            }
        };
    }
}
