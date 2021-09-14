package thito.nodeflow.java.generated.body;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.*;

public abstract class BodyAccessor extends Reference {
    public BodyAccessor(IClass type) {
        super(type);
    }

    public abstract LField getParameter(int index);
    public abstract LField getLocal(int index);
    public abstract LField createLocal(IClass type);
    public abstract IMember getDeclaringMember();

    public void Return() {
        if (MethodContext.hasContext()) {
            MethodContext context = MethodContext.getContext();
            context.pushNode(new InsnNode(Opcodes.RETURN));
        } else if (SourceCode.hasContext()) {
            SourceCode.getContext().getLine().append("return;");
        } else throw new IllegalStateException("no context");
    }

}
