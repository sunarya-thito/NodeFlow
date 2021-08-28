package thito.nodeflow.java.known;

import thito.nodeflow.java.*;

import java.lang.reflect.*;
import java.util.*;

public class KMethod extends AbstractMethod {
    private Method wrapped;

    public KMethod(Method wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
        this.modifiers = wrapped.getModifiers();
    }

    @Override
    public Annotated[] getAnnotations() {
        return Arrays.stream(getWrapped().getAnnotations()).map(Annotated::annotation).toArray(Annotated[]::new);
    }

    public Method getWrapped() {
        return wrapped;
    }

    @Override
    public IClass[] getThrows() {
        return Arrays.stream(getWrapped().getExceptionTypes()).map(Java::Class).toArray(IClass[]::new);
    }

    @Override
    public int getParameterCount() {
        return getWrapped().getParameterCount();
    }

    @Override
    public IClass getReturnType() {
        return Java.Class(getWrapped().getReturnType());
    }

    @Override
    public IClass[] getParameterTypes() {
        return Arrays.stream(getWrapped().getParameterTypes()).map(Java::Class).toArray(IClass[]::new);
    }
}
