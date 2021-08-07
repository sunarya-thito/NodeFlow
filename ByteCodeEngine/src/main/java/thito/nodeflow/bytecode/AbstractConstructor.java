package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractConstructor extends AbstractMember implements IConstructor {
    public AbstractConstructor(IClass declaringClass) {
        super("<init>", declaringClass);
    }

    @Override
    public Reference newInstance(Object... args) {
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
        final MethodContext context = MethodContext.getContext();
        return new Reference(getDeclaringClass()) {
            @Override
            public void write() {
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
        };
    }

    @Override
    public void newInstanceVoid(Object... args) {
        if (args.length != getParameterCount()) {
            throw new IllegalArgumentException("mismatch argument count");
        }
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
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof IConstructor && Arrays.equals(getParameterTypes(), ((IConstructor) obj).getParameterTypes());
    }
}
