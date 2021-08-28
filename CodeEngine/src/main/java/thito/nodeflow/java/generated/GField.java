package thito.nodeflow.java.generated;

import thito.nodeflow.java.*;

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
    public Annotated[] getAnnotations() {
        return annotatedList.toArray(new Annotated[0]);
    }

    @Override
    public IClass getType() {
        return type;
    }

    public GField annotated(Annotated annotated) {
        annotatedList.add(annotated);
        return this;
    }

    public List<Annotated> getAnnotatedList() {
        return annotatedList;
    }
}
