package thito.nodeflow.bytecode.known;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class KConstructor extends AbstractConstructor {
    private Constructor<?> wrapped;

    public KConstructor(Constructor<?> wrapped) {
        super(Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
    }

    public Constructor<?> getWrapped() {
        return wrapped;
    }

    @Override
    public int getParameterCount() {
        return wrapped.getParameterCount();
    }

    @Override
    public IClass[] getParameterTypes() {
        return Arrays.stream(wrapped.getParameterTypes()).map(Java::Class).toArray(IClass[]::new);
    }
}
