package thito.nodeflow.java;

import org.jetbrains.annotations.*;
import thito.nodeflow.java.util.*;

import java.util.*;

public interface IClass extends IMember {
    String getPackageName();
    IClass[] getInterfaces();
    IClass getSuperClass();
    String getSimpleName();
    String getCanonicalName();
    IField[] getFields();
    IField getField(String name);
    IField[] getDeclaredFields();
    IField getDeclaredField(String name);
    IMethod[] getMethods();
    IMethod getMethod(String name, IClass...parameterTypes);
    IMethod[] getDeclaredMethods();
    IMethod getDeclaredMethod(String name, IClass...parameterTypes);
    IConstructor[] getConstructors();
    IConstructor getConstructor(IClass...parameterTypes);
    IConstructor[] getDeclaredConstructors();
    IConstructor getDeclaredConstructor(IClass...parameterTypes);
    IClass[] getDeclaredClasses();
    IClass getDeclaredClass(String name);
    IClass[] getClasses();
    IClass getClass(String name);
    default boolean isAssignableFrom(IClass type) {
        return BCHelper.isAssignableFrom(this, type, new HashSet<>()) || Conversion.isTransformationPossible(this, type);
    }

    boolean isArray();
    IClass getComponentType();

    @Contract(pure = true)
    default Reference field(String name) {
        return getField(name).get(null);
    }

    default StaticMethodInvocation method(String name, IClass... parameterTypes) {
        return new StaticMethodInvocation() {
            @Override
            public Reference invoke(Object... args) {
                return getMethod(name, parameterTypes).invoke(null, args);
            }

            @Override
            public void invokeVoid(Object... args) {
                getMethod(name, parameterTypes).invokeVoid(null, args);
            }
        };
    }

    interface StaticMethodInvocation {
        @Contract(pure = true)
        Reference invoke(Object...args);
        void invokeVoid(Object...args);
    }
}
