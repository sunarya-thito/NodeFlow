package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.*;

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
        final MethodContext context = MethodContext.getContext();
        return new Reference(getReturnType()) {
            @Override
            public void write() {
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
        };
    }

    @Override
    public void invokeVoid(Object instance, Object... args) {
        if (isVoid()) {
            throw new IllegalArgumentException("void method requires invokeVoid");
        }
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
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
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof IMethod && Arrays.equals(getParameterTypes(), ((IMethod) obj).getParameterTypes());
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(getParameterTypes());
    }
}
