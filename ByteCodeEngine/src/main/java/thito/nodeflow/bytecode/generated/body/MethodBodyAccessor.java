package thito.nodeflow.bytecode.generated.body;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.*;

import java.lang.reflect.Type;
import java.util.*;

public class MethodBodyAccessor extends BodyAccessor {
    private GMethod method;
    private List<LField> locals = new ArrayList<>();
    public MethodBodyAccessor(GMethod method) {
        super(method.getReturnType());
        this.method = method;
    }

    @Override
    public LField getParameter(int index) {
        return new LField(method.getParameterTypeList().get(index), index + 1);
    }

    @Override
    public LField createLocal(IClass type) {
        LField field = new LField(type, method.getParameterCount() + 1 + locals.size());
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
    public GMethod getDeclaringMember() {
        return method;
    }
}
