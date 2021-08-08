package thito.nodeflow.bytecode.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;

public class Math {
    private Math() {}

    public static Reference negate(Object a) {
        IClass type = BCHelper.getType(a);
        return new Reference(type) {
            @Override
            public void write() {
                BCHelper.writeToContext(type, a);
                MethodContext.getContext().pushNode(new InsnNode(BCHelper.getASMType(type).getOpcode(Opcodes.INEG)));
            }
        };
    }

    public static Reference add(Object a, Object b) {
        return mathOperation(Opcodes.IADD, a, b);
    }

    public static Reference subtract(Object a, Object b) {
        return mathOperation(Opcodes.ISUB, a, b);
    }

    public static Reference multiply(Object a, Object b) {
        return mathOperation(Opcodes.IMUL, a, b);
    }

    public static Reference divide(Object a, Object b) {
        return mathOperation(Opcodes.IDIV, a, b);
    }

    public static Reference modulo(Object a, Object b) {
        return mathOperation(Opcodes.IREM, a, b);
    }

    public static Reference bitShiftLeft(Object left, Object right) {
        return mathOperation(Opcodes.ISHL, left, right);
    }

    public static Reference bitShiftRight(Object left, Object right) {
        return mathOperation(Opcodes.ISHR, left, right);
    }

    public static Reference bitShiftRightUnsigned(Object left, Object right) {
        return mathOperation(Opcodes.IUSHR, left, right);
    }

    public static Reference and(Object a, Object b) {
        return mathOperation(Opcodes.IAND, a, b);
    }

    public static Reference or(Object a, Object b) {
        return mathOperation(Opcodes.IOR, a, b);
    }

    public static Reference xor(Object a, Object b) {
        return mathOperation(Opcodes.IXOR, a, b);
    }

    private static Reference mathOperation(int opcode, Object a, Object b) {
        IClass priority = BCHelper.getPrioritized(BCHelper.getType(a), BCHelper.getType(b));
        return new Reference(priority) {
            @Override
            public void write() {
                BCHelper.writeToContext(priority, a);
                BCHelper.writeToContext(priority, b);
                MethodContext.getContext().pushNode(new InsnNode(BCHelper.getASMType(priority).getOpcode(opcode)));
            }
        };
    }
}
