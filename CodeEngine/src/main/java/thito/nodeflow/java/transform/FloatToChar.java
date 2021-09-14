package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class FloatToChar implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(char.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.F2I));
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.I2C));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(char) ");
                source.writeSourceCode();
            }
        };
    }
}
