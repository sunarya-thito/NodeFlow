package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class LongToFloat implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(float.class) {
            @Override
            public void write() {
                source.write();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.L2F));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(float) ");
                source.writeSourceCode();
            }
        };
    }
}
