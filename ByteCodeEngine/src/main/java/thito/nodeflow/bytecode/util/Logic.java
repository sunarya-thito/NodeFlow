package thito.nodeflow.bytecode.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;

public class Logic {
    public static Reference And(Object a, Object b) {
        MethodContext context = MethodContext.getContext();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                If.IsTrue(a).Then(() -> {
                    If.IsTrue(b).Then(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_1));
                    }).Else(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_0));
                    });
                }).Else(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_0));
                });
            }
        };
    }
    public static Reference Or(Object a, Object b) {
        MethodContext context = MethodContext.getContext();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                If.IsTrue(a).Then(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_1));
                }).Else(() -> {
                    If.IsTrue(b).Then(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_1));
                    }).Else(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_0));
                    });
                });
            }
        };
    }
    public static Reference ExclusiveOr(Object a, Object b) {
        MethodContext context = MethodContext.getContext();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                If.IsTrue(a).Then(() -> {
                    If.IsFalse(b).Then(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_1));
                    }).Else(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_0));
                    });
                }).Else(() -> {
                    If.IsTrue(b).Then(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_1));
                    }).Else(() -> {
                        context.pushNode(new InsnNode(Opcodes.ICONST_0));
                    });
                });
            }
        };
    }
    public static Reference Negate(Object a) {
        MethodContext context = MethodContext.getContext();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                If.IsTrue(a).Then(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_0));
                }).Else(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_1));
                });
            }
        };
    }
}
