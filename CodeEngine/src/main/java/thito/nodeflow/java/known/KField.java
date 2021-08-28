package thito.nodeflow.java.known;

import thito.nodeflow.java.*;

import java.lang.reflect.*;
import java.util.*;

public class KField extends AbstractField {
    private Field wrapped;

    public KField(Field wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
        this.modifiers = wrapped.getModifiers();
    }

    @Override
    public Annotated[] getAnnotations() {
        return Arrays.stream(getWrapped().getAnnotations()).map(Annotated::annotation).toArray(Annotated[]::new);
    }

    @Override
    public IClass getType() {
        return Java.Class(wrapped.getType());
    }

    public Field getWrapped() {
        return wrapped;
    }
}
