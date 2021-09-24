package thito.nodeflow.java;

import org.jetbrains.annotations.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.known.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;

public class Java {
    @Contract(pure = true)
    public static Reference Null() {
        return new Reference(Java.Class(Object.class)) {
            @Override
            public void writeByteCode() {
                MethodContext context = MethodContext.getContext();
                context.pushNode(new InsnNode(Opcodes.ACONST_NULL));
            }

            @Override
            public void writeSourceCode() {
                SourceCode.getContext().getLine().append("null");
            }
        };
    }

    public static IClass ArrayClass(IClass componentType, int dimensions) {
        return new ArrayClass(componentType, dimensions);
    }

    @Contract(pure = true)
    public static Reference Type(IClass clazz) {
        return new Reference(Class.class) {
            @Override
            public void writeByteCode() {
                MethodContext.getContext().pushNode(new LdcInsnNode(BCHelper.getASMType(clazz)));
            }

            @Override
            public void writeSourceCode() {
                SourceCode sourceCode = SourceCode.getContext();
                StringBuilder line = sourceCode.getLine();
                line.append(sourceCode.simplifyType(clazz));
                line.append(".class");
            }
        };
    }

    public static void Throw(Object throwable) {
        if (MethodContext.hasContext()) {
            MethodContext context = MethodContext.getContext();
            BCHelper.writeToContext(Java.Class(Throwable.class), throwable);
            context.pushNode(new InsnNode(Opcodes.ATHROW));
        } else if (SourceCode.hasContext()) {
            SourceCode code = SourceCode.getContext();
            code.getLine().append("throw ");
            BCHelper.writeToSourceCode(Java.Class(Throwable.class), throwable);
        } else throw new IllegalStateException("no context");
    }

    public static IClass Class(Type type) {
        if (type == null) return null;
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
            public void writeByteCode() {
                BCHelper.writeToContext(getType(), obj);
            }

            @Override
            public void writeSourceCode() {
                BCHelper.writeToSourceCode(getType(), obj);
            }
        };
    }
}
