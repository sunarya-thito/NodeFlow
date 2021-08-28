package thito.nodeflow.java.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class Logic {
    public static Reference And(Object a, Object b) {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
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

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(boolean.class), a);
                line.append(" && ");
                BCHelper.writeToSourceCode(Java.Class(boolean.class), b);
                line.append(')');
            }
        };
    }
    public static Reference Or(Object a, Object b) {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
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

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(boolean.class), a);
                line.append(" || ");
                BCHelper.writeToSourceCode(Java.Class(boolean.class), b);
                line.append(')');
            }
        };
    }
    public static Reference ExclusiveOr(Object a, Object b) {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
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


            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(boolean.class), a);
                line.append(" ^ ");
                BCHelper.writeToSourceCode(Java.Class(boolean.class), b);
                line.append(')');
            }
        };
    }
    public static Reference Negate(Object a) {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                If.IsTrue(a).Then(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_0));
                }).Else(() -> {
                    context.pushNode(new InsnNode(Opcodes.ICONST_1));
                });
            }


            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append("!(");
                BCHelper.writeToSourceCode(Java.Class(boolean.class), a);
                line.append(')');
            }
        };
    }
}
