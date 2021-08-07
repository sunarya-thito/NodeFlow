package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.generated.*;
import thito.nodeflow.bytecode.known.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.*;

public class BCHelper {
    private static final Map<IClass, IClass> primitiveToWrapper = new HashMap<>();
    private static final Map<IClass, IClass> wrapperToPrimitive = new HashMap<>();
    private static final String[] PRIMITIVE_PRIORITY = {
            "double",
            "java.lang.Double",
            "long",
            "java.lang.Long",
            "int",
            "java.lang.Integer",
            "char",
            "java.lang.Character",
            "short",
            "java.lang.Short",
            "byte",
            "java.lang.Byte"
    };

    static {
        primitiveToWrapper.put(new KClass(double.class), new KClass(Double.class));
        primitiveToWrapper.put(new KClass(long.class), new KClass(Long.class));
        primitiveToWrapper.put(new KClass(int.class), new KClass(Integer.class));
        primitiveToWrapper.put(new KClass(float.class), new KClass(Float.class));
        primitiveToWrapper.put(new KClass(char.class), new KClass(Character.class));
        primitiveToWrapper.put(new KClass(short.class), new KClass(Short.class));
        primitiveToWrapper.put(new KClass(byte.class), new KClass(Byte.class));
        primitiveToWrapper.put(new KClass(boolean.class), new KClass(Boolean.class));

        primitiveToWrapper.forEach((primitive, wrapper) -> wrapperToPrimitive.put(wrapper, primitive));
    }

    public static IClass primitiveToWrapper(IClass type) {
        if (wrapperToPrimitive.containsKey(type)) return type;
        return primitiveToWrapper.get(type);
    }

    public static IClass wrapperToPrimitive(IClass type) {
        if (primitiveToWrapper.containsKey(type)) return type;
        return wrapperToPrimitive.get(type);
    }

    public static String getMethodDescriptor(IClass returnType, IClass[] parameterTypes) {
        StringBuilder builder = new StringBuilder("()");
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            builder.insert(1, BCHelper.getDescriptor(parameterTypes[i]));
        }
        if (returnType.getName().equals("void")) {
            builder.append("V");
        } else {
            builder.append(getDescriptor(returnType));
        }
        return builder.toString();
    }
    public static org.objectweb.asm.Type getASMType(IClass type) {
        return org.objectweb.asm.Type.getType(getDescriptor(type));
    }
    public static IClass getType(Object object) {
        if (object instanceof Reference) return ((Reference) object).getType();
        return Java.Class(object.getClass());
    }
    public static String getArrayPrefix(int dimensions) {
        char[] chars = new char[dimensions];
        Arrays.fill(chars, '[');
        return new String(chars);
    }
    public static String getClassPath(IClass type) {
        return type.getName().replace('.', '/');
    }
    public static String getDescriptor(IClass type) {
        if (type.equals(Java.Class(int.class))) {
            return "I";
        }
        if (type.equals(Java.Class(double.class))) {
            return "D";
        }
        if (type.equals(Java.Class(long.class))) {
            return "L";
        }
        if (type.equals(Java.Class(byte.class))) {
            return "B";
        }
        if (type.equals(Java.Class(short.class))) {
            return "S";
        }
        if (type.equals(Java.Class(float.class))) {
            return "F";
        }
        if (type.equals(Java.Class(char.class))) {
            return "C";
        }
        if (type.equals(Java.Class(boolean.class))) {
            return "B";
        }
        return "L"+getClassPath(type)+";";
    }
    public static Reference toReference(Object object) {
        if (object == null) return Java.Null();
        if (object instanceof Reference) return (Reference) object;
        if (object instanceof Boolean) {
            if ((Boolean) object) {
                return new Reference(boolean.class) {
                    @Override
                    public void write() {
                        MethodContext.getContext().pushNode(new InsnNode(Opcodes.ICONST_1));
                    }
                };
            } else {
                return new Reference(boolean.class) {
                    @Override
                    public void write() {
                        MethodContext.getContext().pushNode(new InsnNode(Opcodes.ICONST_0));
                    }
                };
            }
        }
        if (object instanceof Integer || object instanceof Byte || object instanceof Short) {
            IClass primitive = wrapperToPrimitive(Java.Class(object.getClass()));
            int value = ((Number) object).intValue();
            if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                    return new Reference(primitive) {
                        @Override
                        public void write() {
                            MethodContext.getContext()
                                    .pushNode(new IntInsnNode(Opcodes.BIPUSH, ((Number) object).intValue()));
                        }
                    };
                }
                return new Reference(primitive) {
                    @Override
                    public void write() {
                        MethodContext.getContext()
                                .pushNode(new IntInsnNode(Opcodes.SIPUSH, ((Number) object).intValue()));
                    }
                };
            }
        }
        return new Reference(Java.Class(object.getClass())) {
            @Override
            public void write() {
                MethodContext.getContext().pushNode(new LdcInsnNode(object));
            }
        };
    }

    public static void writeToContext(IClass expectation, Object object) {
        if (object instanceof Reference) {
            ((Reference) object).impl_write(expectation);
        } else {
            toReference(object).impl_write(expectation);
        }
    }

    public static <T> int indexOf(T[] array, T target) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], target)) {
                return i;
            }
        }
        return -1;
    }
    public static IClass getPrioritized(IClass a, IClass b) {
        int indexA = indexOf(PRIMITIVE_PRIORITY, a.getName());
        int indexB = indexOf(PRIMITIVE_PRIORITY, b.getName());
        return indexA < 0 || indexB < indexA ? a : b;
    }
}
