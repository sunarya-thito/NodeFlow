package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class ByteToDouble implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(double.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.I2D));
            }

            @Override
            public void writeSourceCode() {
                source.writeSourceCode();
            }
        };
    }
}
