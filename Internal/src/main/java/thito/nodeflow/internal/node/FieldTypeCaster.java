package thito.nodeflow.internal.node;

import thito.nodejfx.parameter.converter.*;

import java.lang.reflect.*;

public class FieldTypeCaster implements TypeCaster<Field> {

    private Class<?> javaClass;

    public FieldTypeCaster(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public Field fromSafeObject(Object obj) {
        if (obj instanceof Field) {
            return (Field) obj;
        }
        if (obj instanceof String) {
            try {
                Field field = javaClass.getDeclaredField((String) obj);
                if (field.isEnumConstant()) {
                    return field;
                }
            } catch (Throwable t) {
            }
        }
        return null;
    }

    @Override
    public Object toSafeObject(Field obj) {
        return obj.getName();
    }
}
