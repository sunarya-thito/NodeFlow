package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class DoubleToLong implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(long.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.D2L));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(long) ");
                source.writeSourceCode();
            }
        };
    }
}
