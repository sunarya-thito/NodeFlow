package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public abstract class AbstractField extends AbstractMember implements IField {
    public AbstractField(String name, IClass declaringClass) {
        super(name, declaringClass);
    }

    @Override
    public Reference get(Object instance) {
        return new Reference(getType()) {
            @Override
            public void writeByteCode() {
                final MethodContext context = MethodContext.getContext();
                if (!Modifier.isStatic(getModifiers())) {
                    if (instance == null) throw new IllegalArgumentException("non-static field requires instance");
                    BCHelper.writeToContext(getDeclaringClass(), instance);
                }
                context.pushNode(new FieldInsnNode(
                        Modifier.isStatic(getModifiers()) ? Opcodes.GETSTATIC : Opcodes.GETFIELD,
                        BCHelper.getClassPath(getDeclaringClass()),
                        getName(),
                        BCHelper.getDescriptor(getType())
                ));
            }

            @Override
            public void writeSourceCode() {
                SourceCode code = SourceCode.getContext();
                StringBuilder line = code.getLine();
                if (!Modifier.isStatic(getModifiers())) {
                    if (instance == null) throw new IllegalStateException("non-static field requires instance");
                    BCHelper.writeToSourceCode(getType(), instance);
                } else {
                    line.append(code.simplifyType(getDeclaringClass()));
                }
                line.append('.');
                line.append(getName());
            }
        };
    }

    @Override
    public void set(Object instance, Object value) {
        if (MethodContext.hasContext()) {
            final MethodContext context = MethodContext.getContext();
            if (!Modifier.isStatic(getModifiers())) {
                if (instance == null) throw new IllegalArgumentException("non-static method requires instance");
                BCHelper.writeToContext(getDeclaringClass(), instance);
            }
            BCHelper.writeToContext(getType(), value);
            context.pushNode(new FieldInsnNode(
                    Modifier.isStatic(getModifiers()) ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD,
                    BCHelper.getClassPath(getDeclaringClass()),
                    getName(),
                    BCHelper.getDescriptor(getType())
            ));
        } else if (SourceCode.hasContext()) {
            SourceCode code = SourceCode.getContext();
            StringBuilder line = code.getLine();
            if (!Modifier.isStatic(getModifiers())) {
                if (instance == null) throw new IllegalStateException("non-static method requires instance");
                BCHelper.writeToSourceCode(getType(), instance);
            } else {
                line.append(code.simplifyType(getDeclaringClass()));
            }
            line.append('.');
            line.append(getName());
            line.append(" = ");
            BCHelper.writeToSourceCode(getType(), value);
            line.append(';');
            code.endLine();
        } else throw new IllegalStateException("no context");
    }
    @Override
    public String toString() {
        String toString = getDeclaringClass().getName() + " " + getType().getName() + " " + getName();
        return getModifiers() == 0 ? toString : Modifier.toString(getModifiers()) + " " + toString;
    }
}
