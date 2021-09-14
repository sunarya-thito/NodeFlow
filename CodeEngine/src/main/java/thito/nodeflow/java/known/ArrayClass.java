package thito.nodeflow.java.known;

import thito.nodeflow.java.*;

import java.util.*;

public class ArrayClass extends AbstractClass {
    private static final IMethod[] methods = Arrays.stream(Object.class.getMethods()).map(KMethod::new).toArray(IMethod[]::new);
    private static final IClass superClass = Java.Class(Object.class);

    private IClass component;
    private int dimensions;

    public ArrayClass(IClass component, int dimensions) {
        super(BCHelper.getArrayPrefix(dimensions)+"L"+component.getName()+";", null);
        this.dimensions = dimensions;
        this.component = component;
        this.modifiers = component.getModifiers();
    }

    @Override
    public String getSimpleName() {
        return component.getSimpleName() + "[]".repeat(Math.max(0, dimensions));
    }

    @Override
    public String getCanonicalName() {
        return getName();
    }

    @Override
    public String getPackageName() {
        return component.getPackageName();
    }

    @Override
    public Annotated[] getAnnotations() {
        return new Annotated[0];
    }

    @Override
    public IClass getComponentType() {
        return component;
    }

    @Override
    public IClass[] getInterfaces() {
        return new IClass[0];
    }

    @Override
    public IClass getSuperClass() {
        return superClass;
    }

    @Override
    public IField[] getFields() {
        return new IField[0];
    }

    @Override
    public IField[] getDeclaredFields() {
        return new IField[0];
    }

    @Override
    public IMethod[] getMethods() {
        return methods;
    }

    @Override
    public IMethod[] getDeclaredMethods() {
        return new IMethod[0];
    }

    @Override
    public IConstructor[] getConstructors() {
        return new IConstructor[0];
    }

    @Override
    public IConstructor[] getDeclaredConstructors() {
        return new IConstructor[0];
    }

    @Override
    public IClass[] getDeclaredClasses() {
        return new IClass[0];
    }

    @Override
    public IClass[] getClasses() {
        return new IClass[0];
    }
}
