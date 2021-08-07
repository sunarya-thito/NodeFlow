package thito.nodeflow.bytecode.known;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;

public class KField extends AbstractField {
    private Field wrapped;

    public KField(Field wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
    }

    @Override
    public IClass getType() {
        return Java.Class(wrapped.getType());
    }

    public Field getWrapped() {
        return wrapped;
    }
}
