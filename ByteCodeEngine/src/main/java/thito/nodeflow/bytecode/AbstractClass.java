package thito.nodeflow.bytecode;

import thito.nodeflow.bytecode.known.*;

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
    public String getSimpleName() {
        String name = getName();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            return name.substring(index);
        }
        return name;
    }

    @Override
    public IField getField(String name) {
        return Arrays.stream(getFields()).filter(field -> field.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public IField getDeclaredField(String name) {
        return Arrays.stream(getDeclaredFields()).filter(field -> field.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public IMethod getMethod(String name, IClass... parameterTypes) {
        return Arrays.stream(getMethods()).filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
                .findAny().orElse(null);
    }

    @Override
    public IMethod getDeclaredMethod(String name, IClass... parameterTypes) {
        return Arrays.stream(getDeclaredMethods()).filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
                .findAny().orElse(null);
    }

    @Override
    public IConstructor getConstructor(IClass... parameterTypes) {
        return Arrays.stream(getConstructors()).filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                .findAny().orElse(null);
    }

    @Override
    public IConstructor getDeclaredConstructor(IClass... parameterTypes) {
        return Arrays.stream(getDeclaredConstructors()).filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                .findAny().orElse(null);
    }

    @Override
    public IClass getDeclaredClass(String name) {
        return Arrays.stream(getDeclaredClasses()).filter(clazz -> clazz.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public IClass getClass(String name) {
        return Arrays.stream(getClasses()).filter(clazz -> clazz.getName().equals(name)).findAny().orElse(null);
    }

}
