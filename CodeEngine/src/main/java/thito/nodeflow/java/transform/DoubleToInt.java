package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class DoubleToInt implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(int.class) {
            @Override
            public void write() {
                source.write();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.D2I));
            }


            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(int) ");
                source.writeSourceCode();
            }
        };
    }
}
