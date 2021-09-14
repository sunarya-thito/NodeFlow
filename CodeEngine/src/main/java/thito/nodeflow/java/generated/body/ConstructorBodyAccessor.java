package thito.nodeflow.java.generated.body;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.*;

import java.util.*;

public class ConstructorBodyAccessor extends BodyAccessor {
    public interface SuperConstructorInvocation {
        void invoke(Object...args);
    }
    private final GConstructor constructor;
    private List<LField> locals = new ArrayList<>();
    public ConstructorBodyAccessor(GConstructor constructor) {
        super(constructor.getDeclaringClass());
        this.constructor = constructor;
    }

    public SuperConstructorInvocation Super(IClass...parameterTypes) {
        IClass superClass = constructor.getDeclaringClass().getSuperClass();
        while (superClass != null) {
            try {
                IConstructor constructor = superClass.getConstructor(parameterTypes);
                return new SuperConstructorInvocation() {
                    @Override
                    public void invoke(Object... args) {
                        if (args.length != parameterTypes.length) throw new IllegalArgumentException("invalid argument length");
                        if (MethodContext.hasContext()) {
                            MethodContext context = MethodContext.getContext();
                            context.pushNode(new VarInsnNode(Opcodes.ALOAD, 0));
                            for (int i = 0; i < args.length; i++) {
                                BCHelper.writeToContext(parameterTypes[i], args[1]);
                            }
                            MethodInsnNode node = new MethodInsnNode(
                                    Opcodes.INVOKESPECIAL,
                                    BCHelper.getClassPath(constructor.getDeclaringClass()),
                                    "<init>",
                                    BCHelper.getMethodDescriptor(Java.Class(void.class), constructor.getParameterTypes()),
                                    false);
                            context.pushNode(node);
                        } else if (SourceCode.hasContext()) {
                            SourceCode context = SourceCode.getContext();
                            StringBuilder line = context.getLine();
                            line.append("super(");
                            for (int i = 0; i < args.length; i++) {
                                if (i != 0) {
                                    line.append(", ");
                                }
                                // to prevent ambiguity
                                line.append('(');
                                line.append(context.simplifyType(parameterTypes[i]));
                                line.append(") ");
                                BCHelper.writeToSourceCode(parameterTypes[i], args[i]);
                            }
                            line.append(");");
                        } else throw new IllegalStateException("no context");
                    }
                };
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
            superClass = superClass.getSuperClass();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public LField getParameter(int index) {
        return new LField(constructor.getParameterTypeList().get(index), index + 1);
    }

    @Override
    public LField createLocal(IClass type) {
        LField field = new LField(type, constructor.getParameterCount() + 1 + locals.size());
        locals.add(field);
        return field;
    }

    @Override
    public LField getLocal(int index) {
        return locals.get(index);
    }

    @Override
    public void writeByteCode() {
        MethodContext context = MethodContext.getContext();
        context.pushNode(new VarInsnNode(Opcodes.ALOAD, 0));
    }

    @Override
    public void writeSourceCode() {
        SourceCode.getContext().getLine().append("this");
    }

    @Override
    public GConstructor getDeclaringMember() {
        return constructor;
    }
}
