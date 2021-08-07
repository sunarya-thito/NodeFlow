package thito.nodeflow.bytecode.generated;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class GField extends AbstractField {
    private IClass type;
    private List<Annotated> annotatedList = new ArrayList<>();
    public GField(String name, IClass declaringClass, IClass type) {
        super(name, declaringClass);
        this.type = type;
    }

    public GField setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public GField setType(IClass type) {
        this.type = type;
        return this;
    }

    @Override
    public IClass getType() {
        return type;
    }

    public GField annotated(String name, Object value) {
        annotatedList.add(new Annotated(name, value));
        return this;
    }

    public List<Annotated> getAnnotatedList() {
        return annotatedList;
    }
}
