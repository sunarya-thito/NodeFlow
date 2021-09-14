package thito.nodeflow.java;

import thito.nodeflow.java.known.*;

import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractClass extends AbstractMember implements IClass {
    public AbstractClass(String name, IClass declaringClass) {
        super(name, declaringClass);
    }

    @Override
    public boolean isArray() {
        return this instanceof ArrayClass;
    }

    @Override
    public IField getField(String name) {
        return Arrays.stream(getFields()).filter(field -> field.getName().equals(name)).findAny().orElseThrow(() -> new NoSuchElementException(name+" in "+getName()));
    }

    @Override
    public IField getDeclaredField(String name) {
        return Arrays.stream(getDeclaredFields()).filter(field -> field.getName().equals(name)).findAny().orElseThrow(() -> new NoSuchElementException(name+" in "+getName()));
    }

    @Override
    public IMethod getMethod(String name, IClass... parameterTypes) {
        return Arrays.stream(getMethods()).filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
                .findAny().orElseThrow(() -> new NoSuchElementException(name+"("+BCHelper.toStringParams(parameterTypes)+") "+getName()));
    }

    @Override
    public IMethod getDeclaredMethod(String name, IClass... parameterTypes) {
        return Arrays.stream(getDeclaredMethods()).filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
                .findAny().orElseThrow(() -> new NoSuchElementException(name+"("+BCHelper.toStringParams(parameterTypes)+") in "+getName()));
    }

    @Override
    public IConstructor getConstructor(IClass... parameterTypes) {
        return Arrays.stream(getConstructors()).filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                .findAny().orElseThrow(() -> new NoSuchElementException("<init>("+BCHelper.toStringParams(parameterTypes)+") in "+getName()));
    }

    @Override
    public IConstructor getDeclaredConstructor(IClass... parameterTypes) {
        return Arrays.stream(getDeclaredConstructors()).filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                .findAny().orElseThrow(() -> new NoSuchElementException("<init>("+BCHelper.toStringParams(parameterTypes)+") in "+getName()));
    }

    @Override
    public IClass getDeclaredClass(String name) {
        return Arrays.stream(getDeclaredClasses()).filter(clazz -> clazz.getName().equals(name)).findAny().orElseThrow(() -> new NoSuchElementException(name+" in "+getName()));
    }

    @Override
    public IClass getClass(String name) {
        return Arrays.stream(getClasses()).filter(clazz -> clazz.getName().equals(name)).findAny().orElseThrow(() -> new NoSuchElementException(name+" in "+getName()));
    }

    @Override
    public String toString() {
        return getModifiers() == 0 ? getName() : Modifier.toString(getModifiers()) + " " + getName();
    }
}
