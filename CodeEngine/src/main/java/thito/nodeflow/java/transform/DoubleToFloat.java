package thito.nodeflow.java.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class DoubleToFloat implements ObjectTransformation {
    @Override
    public Reference transform(Reference source) {
        return new Reference(float.class) {
            @Override
            public void write() {
                source.write();
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.D2F));
            }


            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("(float) ");
                source.writeSourceCode();
            }
        };
    }
}
