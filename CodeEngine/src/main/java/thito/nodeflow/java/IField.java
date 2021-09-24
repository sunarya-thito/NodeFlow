package thito.nodeflow.java;

import org.jetbrains.annotations.*;

public interface IField extends IMember {
    IClass getType();
    @Contract(pure = true)
    Reference get(Object instance);
    void set(Object instance, Object value);
}
