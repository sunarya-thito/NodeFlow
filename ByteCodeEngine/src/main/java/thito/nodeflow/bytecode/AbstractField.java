package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;

public abstract class AbstractField extends AbstractMember implements IField {
    public AbstractField(String name, IClass declaringClass) {
        super(name, declaringClass);
    }

    @Override
    public Reference get(Object instance) {
        final MethodContext context = MethodContext.getContext();
        return new Reference(getType()) {
            @Override
            public void write() {
                if (!Modifier.isStatic(getModifiers())) {
                    if (instance == null) throw new IllegalArgumentException("non-static method requires instance");
                    BCHelper.writeToContext(getDeclaringClass(), instance);
                }
                context.pushNode(new FieldInsnNode(
                        Modifier.isStatic(getModifiers()) ? Opcodes.GETSTATIC : Opcodes.GETFIELD,
                        BCHelper.getClassPath(getDeclaringClass()),
                        getName(),
                        BCHelper.getDescriptor(getType())
                ));
            }
        };
    }

    @Override
    public void set(Object instance, Object value) {
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
    }
}
