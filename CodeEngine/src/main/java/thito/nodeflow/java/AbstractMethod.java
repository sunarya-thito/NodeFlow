package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public abstract class AbstractMethod extends AbstractMember implements IMethod {
    public AbstractMethod(String name, IClass declaringClass) {
        super(name, declaringClass);
    }

    public boolean isVoid() {
        return getReturnType().getName().equals("void");
    }

    @Override
    public Reference invoke(Object instance, Object... args) {
        if (isVoid()) {
            throw new IllegalArgumentException("void method requires invokeVoid");
        }
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
        return new Reference(getReturnType()) {
            @Override
            public void writeByteCode() {
                final MethodContext context = MethodContext.getContext();
                if (!Modifier.isStatic(getModifiers())) {
                    if (instance == null) throw new IllegalArgumentException("non-static method requires instance");
                    BCHelper.writeToContext(getDeclaringClass(), instance);
                }
                IClass[] parameterTypes = getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    BCHelper.writeToContext(parameterTypes[i], args[i]);
                }
                int operation;
                if (Modifier.isPrivate(getModifiers())) {
                    operation = Opcodes.INVOKESPECIAL;
                } else if (Modifier.isStatic(getModifiers())) {
                    operation = Opcodes.INVOKESTATIC;
                } else if (Modifier.isInterface(getDeclaringClass().getModifiers())) {
                    operation = Opcodes.INVOKEINTERFACE;
                } else {
                    operation = Opcodes.INVOKEVIRTUAL;
                }
                context.pushNode(new MethodInsnNode(
                        operation,
                        BCHelper.getClassPath(getDeclaringClass()),
                        getName(),
                        BCHelper.getMethodDescriptor(getReturnType(), getParameterTypes())
                        ));
            }

            @Override
            public void writeSourceCode() {
                SourceCode code = SourceCode.getContext();
                StringBuilder line = code.getLine();
                if (!Modifier.isStatic(getModifiers())) {
                    if (instance == null) throw new IllegalStateException("non-static method requires instance");
                    BCHelper.writeToSourceCode(getDeclaringClass(), instance);
                } else {
                    line.append(code.simplifyType(getDeclaringClass()));
                }
                line.append('.');
                line.append(getName());
                line.append('(');
                IClass[] parameterTypes = getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i != 0) line.append(", ");
                    BCHelper.writeToSourceCode(parameterTypes[i], args[i]);
                }
                line.append(')');
            }
        };
    }

    @Override
    public void invokeVoid(Object instance, Object... args) {
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
        if (MethodContext.hasContext()) {
            final MethodContext context = MethodContext.getContext();
            if (!Modifier.isStatic(getModifiers())) {
                if (instance == null) throw new IllegalArgumentException("non-static method requires instance to invoke");
                BCHelper.writeToContext(getDeclaringClass(), instance);
            }
            IClass[] parameterTypes = getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                BCHelper.writeToContext(parameterTypes[i], args[i]);
            }
            int operation;
            if (Modifier.isPrivate(getModifiers())) {
                operation = Opcodes.INVOKESPECIAL;
            } else if (Modifier.isStatic(getModifiers())) {
                operation = Opcodes.INVOKESTATIC;
            } else if (Modifier.isInterface(getDeclaringClass().getModifiers())) {
                operation = Opcodes.INVOKEINTERFACE;
            } else {
                operation = Opcodes.INVOKEVIRTUAL;
            }
            context.pushNode(new MethodInsnNode(
                    operation,
                    BCHelper.getClassPath(getDeclaringClass()),
                    getName(),
                    BCHelper.getMethodDescriptor(getReturnType(), getParameterTypes())
            ));
            if (!isVoid()) {
                context.pushNode(new InsnNode(BCHelper.getASMType(getReturnType()).getOpcode(Opcodes.POP)));
            }
        } else if (SourceCode.hasContext()) {
            SourceCode code = SourceCode.getContext();
            StringBuilder line = code.getLine();
            if (!Modifier.isStatic(getModifiers())) {
                if (instance == null) throw new IllegalStateException("non-static method requires instance");
                BCHelper.writeToSourceCode(getReturnType(), instance);
            } else {
                line.append(code.simplifyType(getDeclaringClass()));
            }
            line.append('.');
            line.append(getName());
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
        return super.equals(obj) && obj instanceof IMethod && Arrays.equals(getParameterTypes(), ((IMethod) obj).getParameterTypes());
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(getParameterTypes());
    }

    @Override
    public String toString() {
        String toString = getDeclaringClass().getName() + " " + getName() + "(" + Arrays.stream(getParameterTypes()).map(String::valueOf).collect(Collectors.joining(","))+") " + getReturnType().getName();
        return getModifiers() == 0 ? toString : Modifier.toString(getModifiers()) + " " + toString;
    }
}
