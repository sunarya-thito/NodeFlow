package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public abstract class AbstractConstructor extends AbstractMember implements IConstructor {

    public AbstractConstructor(IClass declaringClass) {
        super("<init>", declaringClass);
    }

    @Override
    public Reference newInstance(Object... args) {
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
        return new Reference(getDeclaringClass()) {
            @Override
            public void writeByteCode() {
                final MethodContext context = MethodContext.getContext();
                context.pushNode(new TypeInsnNode(Opcodes.NEW, BCHelper.getClassPath(getDeclaringClass())));
                // will always be DUP, DUP2 or anything else is impossible
//                context.pushNode(new InsnNode(BCHelper.getASMType(getDeclaringClass()).getOpcode(Opcodes.DUP)));
                context.pushNode(new InsnNode(Opcodes.DUP));
                IClass[] parameterTypes = getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    BCHelper.writeToContext(parameterTypes[i], args[i]);
                }
                context.pushNode(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        BCHelper.getClassPath(getDeclaringClass()),
                        getName(),
                        BCHelper.getMethodDescriptor(Java.Class(void.class), getParameterTypes())
                ));
            }

            @Override
            public void writeSourceCode() {
                SourceCode code = SourceCode.getContext();
                StringBuilder line = code.getLine();
                line.append("new ");
                line.append(code.simplifyType(getDeclaringClass()));
                line.append('(');
                IClass[] parameterTypes = getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i != 0) line.append(", ");
                    BCHelper.writeToSourceCode(parameterTypes[i], args[i]);
                }
                line.append(')');
                code.endLine();
            }
        };
    }

    @Override
    public void newInstanceVoid(Object... args) {
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
        if (MethodContext.hasContext()) {
            final MethodContext context = MethodContext.getContext();
            context.pushNode(new TypeInsnNode(Opcodes.NEW, BCHelper.getClassPath(getDeclaringClass())));
            IClass[] parameterTypes = getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                BCHelper.writeToContext(parameterTypes[i], args[i]);
            }
            context.pushNode(new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    BCHelper.getClassPath(getDeclaringClass()),
                    getName(),
                    BCHelper.getMethodDescriptor(Java.Class(void.class), getParameterTypes())
            ));
        } else if (SourceCode.hasContext()) {
            SourceCode code = SourceCode.getContext();
            StringBuilder line = code.getLine();
            line.append("new ");
            line.append(code.simplifyType(getDeclaringClass()));
            line.append('(');
            IClass[] parameterTypes = getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i != 0) line.append(", ");
                BCHelper.writeToSourceCode(parameterTypes[i], args[i]);
            }
            line.append(");");
            code.endLine();
        } else throw new IllegalStateException("no context");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof IConstructor && Arrays.equals(getParameterTypes(), ((IConstructor) obj).getParameterTypes());
    }

    @Override
    public String toString() {
        String toString = getDeclaringClass().getName() + " " + getName() + "(" + Arrays.stream(getParameterTypes()).map(String::valueOf).collect(Collectors.joining(","))+")";
        return getModifiers() == 0 ? toString : Modifier.toString(getModifiers()) + " " + toString;
    }
}
