package thito.nodeflow.java;

import thito.nodeflow.java.util.*;

public abstract class Reference {
    private final IClass type;
    private final SourceCode sourceCodeContext;
    private final MethodContext methodContext;

    {
        sourceCodeContext = SourceCode.hasContext() ? SourceCode.getContext() : null;
        methodContext = MethodContext.hasContext() ? MethodContext.getContext() : null;
    }

    public Reference(Class<?> clazz) {
        this(Java.Class(clazz));
    }
    public Reference(IClass type) {
        this.type = type;
    }

    public IClass getType() {
        return type;
    }

    public abstract void writeByteCode();
    public abstract void writeSourceCode();

    public MethodContext getMethodContext() {
        return methodContext;
    }

    public SourceCode getSourceCodeContext() {
        return sourceCodeContext;
    }

    public void impl_write(IClass expectation) {
        if (expectation.isAssignableFrom(getType())) {
            writeByteCode();
        } else {
            Conversion.cast(this, expectation).writeByteCode();
        }
    }

    public void impl_writeSourceCode(IClass expectation) {
        if (expectation.isAssignableFrom(getType())) {
            writeSourceCode();
        } else {
            Conversion.cast(this, expectation).writeSourceCode();
        }
    }

    public final Reference virtualToString() {
        return method("toString").invoke();
    }

    public final Reference virtualEquals(Object other) {
        return method("equals", Java.Class(Object.class)).invoke(other);
    }

    public Reference field(String name) {
        return getType().getField(name).get(this);
    }

    public void field(String name, Object value) {
        getType().getField(name).set(this, value);
    }

    public MethodInvocation method(String name, IClass... parameterTypes) {
        return new MethodInvocation() {
            @Override
            public Reference invoke(Object... args) {
                return getType().getMethod(name, parameterTypes).invoke(Reference.this, args);
            }

            @Override
            public void invokeVoid(Object... args) {
                getType().getMethod(name, parameterTypes).invokeVoid(Reference.this, args);
            }
        };
    }

    public interface MethodInvocation {
        Reference invoke(Object...args);
        void invokeVoid(Object...args);
    }
}
