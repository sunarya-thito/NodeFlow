package thito.nodeflow.bytecode.known;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class KClass extends AbstractClass {
    private Class<?> wrapped;

    public KClass(Class<?> wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
    }

    @Override
    public boolean isAssignableFrom(IClass type) {
        if (equals(type)) return true;
        // TODO
        return false;
    }

    @Override
    public IClass getComponentType() {
        return Java.Class(wrapped.getComponentType());
    }

    @Override
    public IClass[] getInterfaces() {
        return Arrays.stream(wrapped.getInterfaces()).map(Java::Class).toArray(IClass[]::new);
    }

    @Override
    public IClass getSuperClass() {
        return Java.Class(wrapped.getSuperclass());
    }

    public Class<?> getWrapped() {
        return wrapped;
    }

    @Override
    public IField[] getFields() {
        return Arrays.stream(wrapped.getFields()).map(KField::new).toArray(IField[]::new);
    }

    @Override
    public IField[] getDeclaredFields() {
        return Arrays.stream(wrapped.getDeclaredFields()).map(KField::new).toArray(IField[]::new);
    }

    @Override
    public IMethod[] getMethods() {
        return Arrays.stream(wrapped.getMethods()).map(KMethod::new).toArray(IMethod[]::new);
    }

    @Override
    public IMethod[] getDeclaredMethods() {
        return Arrays.stream(wrapped.getDeclaredMethods()).map(KMethod::new).toArray(IMethod[]::new);
    }

    @Override
    public IConstructor[] getConstructors() {
        return Arrays.stream(wrapped.getConstructors()).map(KConstructor::new).toArray(IConstructor[]::new);
    }

    @Override
    public IConstructor[] getDeclaredConstructors() {
        return Arrays.stream(wrapped.getDeclaredConstructors()).map(KConstructor::new).toArray(IConstructor[]::new);
    }

    @Override
    public IClass[] getDeclaredClasses() {
        return Arrays.stream(wrapped.getDeclaredClasses()).map(Java::Class).toArray(IClass[]::new);
    }

    @Override
    public IClass[] getClasses() {
        return Arrays.stream(wrapped.getClasses()).map(Java::Class).toArray(IClass[]::new);
    }
}
