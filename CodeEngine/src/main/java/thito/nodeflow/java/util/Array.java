package thito.nodeflow.java.util;

import org.jetbrains.annotations.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class Array {
    @Contract(pure = true)
    public static Reference get(Object array, Object index) {
        IClass finalComponent = BCHelper.getType(array).getComponentType();
        return new Reference(finalComponent) {
            @Override
            public void writeByteCode() {
                BCHelper.writeToContext(Java.Class(Object.class), array);
                BCHelper.writeToContext(Java.Class(int.class), index);
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.AALOAD));
            }

            @Override
            public void writeSourceCode() {
                SourceCode sourceCode = SourceCode.getContext();
                StringBuilder line = sourceCode.getLine();
                BCHelper.writeToSourceCode(Java.Class(Object.class), array);
                line.append('[');
                BCHelper.writeToSourceCode(Java.Class(int.class), index);
                line.append(']');
            }
        };
    }
    public static void set(Object array, Object index, Object value) {
        if (MethodContext.hasContext()) {
            MethodContext context = MethodContext.getContext();
            BCHelper.writeToContext(Java.Class(Object.class), array);
            BCHelper.writeToContext(Java.Class(int.class), index);
            BCHelper.writeToContext(BCHelper.getType(array).getComponentType(), value);
            context.pushNode(new InsnNode(Opcodes.AASTORE));
        } else if (SourceCode.hasContext()) {
            SourceCode sourceCode = SourceCode.getContext();
            StringBuilder line = sourceCode.getLine();
            BCHelper.writeToSourceCode(Java.Class(Object.class), array);
            line.append('[');
            BCHelper.writeToSourceCode(Java.Class(int.class), array);
            line.append("] = ");
            BCHelper.writeToSourceCode(BCHelper.getType(array).getComponentType(), value);
            line.append(';');
            sourceCode.endLine();
        } else throw new IllegalStateException("no context");
    }
    @Contract(pure = true)
    public static Reference getLength(Object array) {
        return new Reference(int.class) {
            @Override
            public void writeByteCode() {
                BCHelper.writeToContext(Java.Class(Object.class), array);
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.ARRAYLENGTH));
            }

            @Override
            public void writeSourceCode() {
                BCHelper.writeToSourceCode(Java.Class(Object.class), array);
                SourceCode.getContext().getLine().append(".length");
            }
        };
    }
    @Contract(pure = true)
    public static Reference newArray(IClass type, Object...values) {
        return new Reference(Java.ArrayClass(type, 1)) {
            @Override
            public void writeByteCode() {
                BCHelper.writeToContext(Java.Class(int.class), values.length);
                MethodContext context = MethodContext.getContext();
                context.pushNode(new TypeInsnNode(Opcodes.ANEWARRAY, BCHelper.getClassPath(type)));
                for (int i = 0; i < values.length; i++) {
                    context.pushNode(new InsnNode(Opcodes.DUP));
                    BCHelper.writeToContext(Java.Class(int.class), i);
                    BCHelper.writeToContext(type, values[i]);
                    context.pushNode(new InsnNode(Opcodes.AASTORE));
                }
            }

            @Override
            public void writeSourceCode() {
                SourceCode sourceCode = SourceCode.getContext();
                sourceCode.getLine().append("new ");
                sourceCode.getLine().append(sourceCode.simplifyType(type));
                sourceCode.getLine().append("[] {");
                for (int i = 0; i < values.length; i++) {
                    if (i != 0) {
                        sourceCode.getLine().append(", ");
                    }
                    BCHelper.writeToSourceCode(type, values[i]);
                }
                sourceCode.getLine().append('}');
            }
        };
    }
    @Contract(pure = true)
    public static Reference newInstance(IClass type, Object...dimensions) {
        if (dimensions.length == 1) {
            return new Reference(Java.ArrayClass(type, 1)) {
                @Override
                public void writeByteCode() {
                    BCHelper.writeToContext(Java.Class(int.class), dimensions[0]);
                    MethodContext.getContext().pushNode(new TypeInsnNode(Opcodes.ANEWARRAY, BCHelper.getClassPath(type)));
                }

                @Override
                public void writeSourceCode() {
                    SourceCode sourceCode = SourceCode.getContext();
                    StringBuilder line = sourceCode.getLine();
                    line.append("new ");
                    line.append(sourceCode.simplifyType(type));
                    line.append('[');
                    BCHelper.writeToSourceCode(Java.Class(int.class), dimensions[0]);
                    line.append(']');
                }
            };
        }
        return new Reference(Java.ArrayClass(type, dimensions.length)) {
            @Override
            public void writeByteCode() {
                for (Object dim : dimensions) {
                    BCHelper.writeToContext(Java.Class(int.class), dim);
                }
                MethodContext.getContext().pushNode(new MultiANewArrayInsnNode(BCHelper.getArrayPrefix(dimensions.length)+BCHelper.getDescriptor(type), dimensions.length));
            }

            @Override
            public void writeSourceCode() {
                SourceCode sourceCode = SourceCode.getContext();
                StringBuilder line = sourceCode.getLine();
                line.append("new ");
                line.append(sourceCode.simplifyType(type));
                line.append('[');
                for (int i = 0; i < dimensions.length; i++) {
                    if (i != 0) line.append("][");
                    BCHelper.writeToSourceCode(Java.Class(int.class), dimensions[i]);
                }
                line.append(']');
            }
        };
    }
}
