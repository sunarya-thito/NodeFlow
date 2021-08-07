package thito.nodeflow.bytecode.known;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class KMethod extends AbstractMethod {
    private Method wrapped;

    public KMethod(Method wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
    }

    public Method getWrapped() {
        return wrapped;
    }

    @Override
    public int getParameterCount() {
        return wrapped.getParameterCount();
    }

    @Override
    public IClass getReturnType() {
        return Java.Class(wrapped.getReturnType());
    }

    @Override
    public IClass[] getParameterTypes() {
        return Arrays.stream(wrapped.getParameterTypes()).map(Java::Class).toArray(IClass[]::new);
    }
}
