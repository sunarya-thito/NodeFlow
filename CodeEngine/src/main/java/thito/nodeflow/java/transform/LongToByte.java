package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class LongToByte implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(byte.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.L2I));
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.I2B));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(byte) ");
                source.writeSourceCode();
            }
        };
    }
}
