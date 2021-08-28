package thito.nodeflow.java.generated.body;

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

}
