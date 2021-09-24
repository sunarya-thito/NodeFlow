package thito.nodeflow.java;

import org.jetbrains.annotations.*;

public interface IConstructor extends IMember {
    int getParameterCount();
    IClass[] getParameterTypes();
    @Contract(pure = true)
    Reference newInstance(Object...args);
    void newInstanceVoid(Object...args);
    IClass[] getThrows();
}
