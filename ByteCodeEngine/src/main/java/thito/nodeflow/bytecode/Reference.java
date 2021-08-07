package thito.nodeflow.bytecode;

import thito.nodeflow.bytecode.util.*;

public abstract class Reference {
    private final IClass type;

    public Reference(Class<?> clazz) {
        this(Java.Class(clazz));
    }
    public Reference(IClass type) {
        this.type = type;
    }

    public IClass getType() {
        return type;
    }

    public abstract void write();

    public final void impl_write(IClass expectation) {
        if (expectation.isAssignableFrom(getType())) {
            write();
        } else {
            Conversion.cast(this, expectation).write();
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
