package thito.nodeflow.java.known;

import thito.nodeflow.java.*;

import java.lang.reflect.*;
import java.util.*;

public class KConstructor extends AbstractConstructor {
    private Constructor<?> wrapped;

    public KConstructor(Constructor<?> wrapped) {
        super(Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
        this.modifiers = wrapped.getModifiers();
    }

    @Override
    public Annotated[] getAnnotations() {
        return Arrays.stream(getWrapped().getAnnotations()).map(Annotated::annotation).toArray(Annotated[]::new);
    }

    @Override
    public IClass[] getThrows() {
        return Arrays.stream(getWrapped().getExceptionTypes()).map(Java::Class).toArray(IClass[]::new);
    }

    public Constructor<?> getWrapped() {
        return wrapped;
    }

    @Override
    public int getParameterCount() {
        return getWrapped().getParameterCount();
    }

    @Override
    public IClass[] getParameterTypes() {
        return Arrays.stream(getWrapped().getParameterTypes()).map(Java::Class).toArray(IClass[]::new);
    }
}
