package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class DoubleToShort implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(short.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.D2I));
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.I2S));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(short) ");
                source.writeSourceCode();
            }
        };
    }
}
