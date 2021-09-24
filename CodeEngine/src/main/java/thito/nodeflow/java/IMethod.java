package thito.nodeflow.java;

import org.jetbrains.annotations.*;

public interface IMethod extends IMember {
    int getParameterCount();
    IClass getReturnType();
    IClass[] getParameterTypes();
    @Contract(pure = true)
    Reference invoke(Object instance, Object...args);
    void invokeVoid(Object instance, Object...args);
    IClass[] getThrows();
}
