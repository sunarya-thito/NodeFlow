package thito.nodeflow.java.util;

import org.jetbrains.annotations.*;
import thito.nodeflow.java.*;

public class EnumType {

    public static EnumType Enum(IClass enumType) {
        return new EnumType(enumType);
    }

    private IClass enumType;

    public EnumType(IClass enumType) {
        this.enumType = enumType;
    }

    public IClass getEnumType() {
        return enumType;
    }

    @Contract(pure = true)
    public Reference valueOf(Object name) {
        return enumType.method("valueOf", Java.Class(String.class)).invoke(name);
    }

    @Contract(pure = true)
    public Reference values() {
        return enumType.method("values").invoke();
    }
}
