package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class FloatToLong implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(long.class) {
            @Override
            public void write() {
                source.write();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.F2L));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(long) ");
                source.writeSourceCode();
            }
        };
    }
}
