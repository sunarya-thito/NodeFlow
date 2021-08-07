package thito.nodeflow.bytecode.generated.body;

import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.*;

import java.lang.reflect.*;

public abstract class BodyAccessor extends Reference {
    public BodyAccessor(IClass type) {
        super(type);
    }

    public abstract LField getParameter(int index);
    public abstract LField getLocal(int index);
    public abstract LField createLocal(IClass type);
    public abstract IMember getDeclaringMember();

}
