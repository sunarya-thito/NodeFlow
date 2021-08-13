package thito.nodeflow.library.reflection;

import java.lang.reflect.*;

public class ReflectionHelper {
    public static void forceSet(Class<?> clazz, String fieldName, Object instance, Object value) {
        try {
            Field field = Field.class.getDeclaredField("modifiers");
            field.setAccessible(true);
            Field target = clazz.getDeclaredField(fieldName);
            field.set(field, target.getModifiers() & ~Modifier.FINAL);
            target.setAccessible(true);
            target.set(instance, value);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
