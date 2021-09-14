package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class FloatToInt implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(int.class) {
            @Override
            public void writeByteCode() {
                source.writeByteCode();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.F2I));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(int) ");
                source.writeSourceCode();
            }
        };
    }
}
