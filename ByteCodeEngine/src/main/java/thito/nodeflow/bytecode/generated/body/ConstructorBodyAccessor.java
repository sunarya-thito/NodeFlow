package thito.nodeflow.bytecode.generated.body;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.*;

import java.lang.reflect.Type;
import java.util.*;

public class ConstructorBodyAccessor extends BodyAccessor {
    private final GConstructor constructor;
    private List<LField> locals = new ArrayList<>();
    public ConstructorBodyAccessor(GConstructor constructor) {
        super(constructor.getDeclaringClass());
        this.constructor = constructor;
    }

    public MethodInvocation Super(IClass...parameterTypes) {
        return method("<init>", parameterTypes);
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
    public void write() {
        MethodContext context = MethodContext.getContext();
        context.pushNode(new VarInsnNode(Opcodes.ALOAD, 0));
    }

    @Override
    public GConstructor getDeclaringMember() {
        return constructor;
    }
}
