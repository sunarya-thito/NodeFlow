package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.generated.*;
import thito.nodeflow.bytecode.known.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;

public class Java {
    public static Reference Null() {
        return new Reference(Java.Class(Object.class)) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                context.pushNode(new InsnNode(Opcodes.ACONST_NULL));
            }
        };
    };
    public static IClass ArrayClass(IClass componentType, int dimensions) {
        return new ArrayClass(componentType, dimensions);
    }
    public static Reference Type(IClass clazz) {
        return new Reference(Class.class) {
            @Override
            public void write() {
                MethodContext.getContext().pushNode(new LdcInsnNode(BCHelper.getASMType(clazz)));
            }
        };
    }
    public static IClass Class(Type type) {
        String name = type.getTypeName();
        Context context = Context.getContext();
        GClass onContext = context.getClass(name);
        if (onContext != null) return onContext;
        if (type instanceof Class) {
            return context.cachedClassMap.computeIfAbsent((Class<?>) type, KClass::new);
        }
        if (type instanceof ParameterizedType) {
            return Class(((ParameterizedType) type).getRawType());
        }
        return new KClass(Object.class);
    }
    public Reference Reference(Object obj) {
        IClass type = Class(obj.getClass());
        return new Reference(type) {
            @Override
            public void write() {
                BCHelper.writeToContext(getType(), obj);
            }
        };
    }
}
