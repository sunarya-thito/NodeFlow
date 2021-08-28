package thito.nodeflow.java.known;

import thito.nodeflow.java.*;

import java.util.*;

public class KClass extends AbstractClass {
    private Class<?> wrapped;

    public KClass(Class<?> wrapped) {
        super(wrapped.getName(), Java.Class(wrapped.getDeclaringClass()));
        this.wrapped = wrapped;
        this.modifiers = wrapped.getModifiers();
    }

    @Override
    public String getSimpleName() {
        return wrapped.getSimpleName();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public String getPackageName() {
        return getWrapped().getPackageName();
    }

    @Override
    public Annotated[] getAnnotations() {
        return Arrays.stream(getWrapped().getAnnotations()).map(Annotated::annotation).toArray(Annotated[]::new);
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
