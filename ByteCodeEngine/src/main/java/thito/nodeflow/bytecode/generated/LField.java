package thito.nodeflow.bytecode.generated;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;

import java.lang.reflect.Type;

public class LField {
    private IClass type;
    private int index;

    public LField(IClass type, int index) {
        this.type = type;
        this.index = index;
    }

    public IClass getType() {
        return type;
    }

    public int getVirtualIndex() {
        return index;
    }

    public Reference get() {
        final MethodContext context = MethodContext.getContext();
        return new Reference(getType()) {
            @Override
            public void write() {
                context.pushNode(new VarInsnNode(BCHelper.getASMType(getType()).getOpcode(Opcodes.ILOAD), getVirtualIndex()));
            }
        };
    }

    public void set(Object value) {
        final MethodContext context = MethodContext.getContext();
        context.pushNode(new VarInsnNode(BCHelper.getASMType(getType()).getOpcode(Opcodes.ISTORE), getVirtualIndex()));
    }
}
