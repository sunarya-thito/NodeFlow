package thito.nodeflow.java;

import org.apache.commons.text.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.known.*;
import thito.nodeflow.java.util.*;

import java.util.*;
import java.util.stream.*;

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
        primitiveToWrapper.put(new KClass(void.class), new KClass(Void.class));

        primitiveToWrapper.forEach((primitive, wrapper) -> wrapperToPrimitive.put(wrapper, primitive));
    }

    public static boolean isPrimitive(IClass iClass) {
        return primitiveToWrapper.containsKey(iClass);
    }

    public static boolean isWrapper(IClass iClass) {
        return wrapperToPrimitive.containsKey(iClass);
    }

    public static String toStringParams(IClass[] classes) {
        return Arrays.stream(classes).map(IClass::getName).collect(Collectors.joining(","));
    }

    public static IClass primitiveToWrapper(IClass type) {
        if (wrapperToPrimitive.containsKey(type)) return type;
        return primitiveToWrapper.get(type);
    }

    public static IClass wrapperToPrimitive(IClass type) {
        if (primitiveToWrapper.containsKey(type)) return type;
        return wrapperToPrimitive.get(type);
    }

    public static boolean isAssignableFrom(IClass source, IClass target, Set<IClass> scanned) {
        if (target == null) return false;
        if (source.getName().equals(target.getName())) return true;
        if (source.isArray()) {
            if (isAssignableFrom(Java.Class(Object.class), target, new HashSet<>())) {
                return true;
            }
            if (target.isArray()) {
                return isAssignableFrom(source.getComponentType(), target.getComponentType(), new HashSet<>());
            }
            return false;
        }
        IClass superClass = target.getSuperClass();
        if (superClass != null && scanned.add(superClass) && isAssignableFrom(source, superClass, scanned)) {
            return true;
        }
        IClass[] interfaces = target.getInterfaces();
        if (interfaces != null) {
            for (IClass in : interfaces) {
                if (scanned.add(in) && isAssignableFrom(source, in, scanned)) {
                    return true;
                }
            }
        }
        return false;
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
        // forces written constant to be primitive values
        return wrapperToPrimitive(Java.Class(object.getClass()));
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
            return "Z";
        }
        if (type.isArray()) return type.getName().replace('.', '/');
        return "L"+getClassPath(type)+";";
    }
    public static Reference toReference(Object object, IClass expectation) {
        if (object == null) return Java.Null();
        if (object instanceof Reference) return (Reference) object;
        if (object.getClass().isArray()) {
            Object[] elements = new Object[java.lang.reflect.Array.getLength(object)];
            for (int i = 0; i < elements.length; i++) elements[i] = java.lang.reflect.Array.get(object, i);
            return Array.newArray(Java.Class(object.getClass().getComponentType()), elements);
        }
        if (object instanceof Enum) {
            return EnumType.Enum(Java.Class(object.getClass())).valueOf(((Enum<?>) object).name());
        }
        if (object instanceof Boolean) {
            if ((Boolean) object) {
                return new ConstantReference(boolean.class, object) {
                    @Override
                    public void writeByteCode() {
                        MethodContext.getContext().pushNode(new InsnNode(Opcodes.ICONST_1));
                    }

                    @Override
                    public void writeSourceCode() {
                        SourceCode.getContext().getLine().append("true");
                    }
                };
            } else {
                return new ConstantReference(boolean.class, object) {
                    @Override
                    public void writeByteCode() {
                        MethodContext.getContext().pushNode(new InsnNode(Opcodes.ICONST_0));
                    }

                    @Override
                    public void writeSourceCode() {
                        SourceCode.getContext().getLine().append("false");
                    }
                };
            }
        }
        if (object instanceof Long) {
            if (Integer.MIN_VALUE <= (Long) object && (Long) object <= Integer.MAX_VALUE) {
                object = ((Long) object).intValue();
            }
        }
        Object finalObject;
        if (object instanceof Integer || object instanceof Byte || object instanceof Short) {
            String name = expectation.getName();
            if (name.equals("java.lang.Double") || name.equals("double")) {
                finalObject = ((Number) object).doubleValue();
            } else if (name.equals("java.lang.Float") || name.equals("float")) {
                finalObject = ((Number) object).floatValue();
            } else {
                finalObject = object;
                IClass primitive = wrapperToPrimitive(Java.Class(object.getClass()));
                int value = ((Number) object).intValue();
                if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                    if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                        return new ConstantReference(primitive, object) {
                            @Override
                            public void writeByteCode() {
                                MethodContext.getContext()
                                        .pushNode(new IntInsnNode(Opcodes.BIPUSH, ((Number) finalObject).intValue()));
                            }

                            @Override
                            public void writeSourceCode() {
                                StringBuilder line = SourceCode.getContext().getLine();
                                if (primitive.equals(Java.Class(byte.class))) {
                                    line.append("(byte) ");
                                }
                                line.append(finalObject);
                            }
                        };
                    }
                    return new ConstantReference(primitive, object) {
                        @Override
                        public void writeByteCode() {
                            MethodContext.getContext()
                                    .pushNode(new IntInsnNode(Opcodes.SIPUSH, ((Number) finalObject).intValue()));
                        }

                        @Override
                        public void writeSourceCode() {
                            StringBuilder line = SourceCode.getContext().getLine();
                            if (primitive.equals(Java.Class(short.class)) || primitive.equals(Java.Class(byte.class))) {
                                line.append("(short) ");
                            }
                            line.append(finalObject);
                        }
                    };
                }
            }
        } else {
            finalObject = object;
        }
        IClass primitive = BCHelper.wrapperToPrimitive(Java.Class(finalObject.getClass()));
        return new ConstantReference(primitive == null ? Java.Class(finalObject.getClass()) : primitive, object) {
            @Override
            public void writeByteCode() {
                MethodContext.getContext().pushNode(new LdcInsnNode(finalObject));
            }

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                if (finalObject instanceof String) {
                    line.append('"');
                    line.append(StringEscapeUtils.unescapeJava((String) finalObject));
                    line.append('"');
                } else if (finalObject instanceof Long) {
                    line.append(finalObject);
                    line.append("L");
                } else if (finalObject instanceof Float) {
                    line.append(finalObject);
                    line.append("F");
                } else {
                    // what else could this be?
                    line.append(finalObject);
                }
            }
        };
    }

    public static void writeToSourceCode(IClass expectation, Object object) {
        if (object instanceof Reference) {
            SourceCode code = SourceCode.getContext();
            if (code != ((Reference) object).getSourceCodeContext())
                throw new IllegalStateException("invalid context scope");
            ((Reference) object).impl_writeSourceCode(expectation);
        } else {
            toReference(object, expectation).impl_writeSourceCode(expectation);
        }
    }

    public static void writeToContext(IClass expectation, Object object) {
        if (object instanceof Reference) {
            MethodContext methodContext = MethodContext.getContext();
            if (methodContext != ((Reference) object).getMethodContext())
                throw new IllegalStateException("invalid context scope");
            ((Reference) object).impl_write(expectation);
        } else {
            toReference(object, expectation).impl_write(expectation);
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
        a = wrapperToPrimitive(a);
        b = wrapperToPrimitive(b);
        int indexA = indexOf(PRIMITIVE_PRIORITY, a.getName());
        int indexB = indexOf(PRIMITIVE_PRIORITY, b.getName());
        return indexA < 0 || indexB < indexA ? b : a;
    }
}
