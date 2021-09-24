package thito.nodeflow.java.util;

import org.jetbrains.annotations.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.transform.*;

import java.util.*;

public class Conversion {
    public static class ClassPair {
        private IClass source, target;

        public ClassPair(IClass source, IClass target) {
            this.source = source;
            this.target = target;
        }

        public IClass getSource() {
            return source;
        }

        public IClass getTarget() {
            return target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassPair)) return false;
            ClassPair classPair = (ClassPair) o;
            return source.equals(classPair.source) && target.equals(classPair.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }
    private static Map<ClassPair, ObjectTransformation> transformationMap = new HashMap<>();
    static {
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(char.class)), new ByteToChar());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(double.class)), new ByteToDouble());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(float.class)), new ByteToFloat());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(int.class)), new ByteToInt());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(long.class)), new ByteToLong());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(short.class)), new ByteToShort());

        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(byte.class)), new CharToByte());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(double.class)), new CharToDouble());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(float.class)), new CharToFloat());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(int.class)), new CharToInt());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(long.class)), new CharToLong());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(short.class)), new CharToShort());

        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(byte.class)), new DoubleToByte());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(char.class)), new DoubleToChar());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(float.class)), new DoubleToFloat());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(int.class)), new DoubleToInt());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(long.class)), new DoubleToLong());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(short.class)), new DoubleToShort());

        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(byte.class)), new FloatToByte());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(char.class)), new FloatToChar());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(double.class)), new FloatToDouble());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(int.class)), new FloatToInt());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(long.class)), new FloatToLong());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(short.class)), new FloatToShort());

        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(byte.class)), new IntToByte());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(char.class)), new IntToChar());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(double.class)), new IntToDouble());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(float.class)), new IntToFloat());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(long.class)), new IntToLong());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(short.class)), new IntToShort());

        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(byte.class)), new ShortToByte());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(char.class)), new ShortToChar());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(double.class)), new ShortToDouble());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(float.class)), new ShortToFloat());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(int.class)), new ShortToInt());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(long.class)), new ShortToLong());

        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(byte.class)), new LongToByte());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(char.class)), new LongToChar());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(double.class)), new LongToDouble());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(float.class)), new LongToFloat());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(int.class)), new LongToInt());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(short.class)), new LongToShort());

        registerTransformation(new ClassPair(Java.Class(Long.class), Java.Class(long.class)), new UnboxingLong());
        registerTransformation(new ClassPair(Java.Class(Double.class), Java.Class(double.class)), new UnboxingDouble());
        registerTransformation(new ClassPair(Java.Class(Integer.class), Java.Class(int.class)), new UnboxingInteger());
        registerTransformation(new ClassPair(Java.Class(Float.class), Java.Class(float.class)), new UnboxingFloat());
        registerTransformation(new ClassPair(Java.Class(Character.class), Java.Class(char.class)), new UnboxingCharacter());
        registerTransformation(new ClassPair(Java.Class(Short.class), Java.Class(short.class)), new UnboxingShort());
        registerTransformation(new ClassPair(Java.Class(Byte.class), Java.Class(byte.class)), new UnboxingByte());
        registerTransformation(new ClassPair(Java.Class(Boolean.class), Java.Class(boolean.class)), new UnboxingBoolean());

        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(Long.class)), new BoxingLong());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(Double.class)), new BoxingDouble());
        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(Integer.class)), new BoxingInt());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(Float.class)), new BoxingFloat());
        registerTransformation(new ClassPair(Java.Class(char.class), Java.Class(Character.class)), new BoxingChar());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(Short.class)), new BoxingShort());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(Byte.class)), new BoxingByte());
        registerTransformation(new ClassPair(Java.Class(boolean.class), Java.Class(Boolean.class)), new BoxingBoolean());

        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(boolean.class)), new StringToBoolean());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(byte.class)), new StringToByte());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(char.class)), new StringToChar());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(double.class)), new StringToDouble());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(float.class)), new StringToFloat());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(int.class)), new StringToInt());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(long.class)), new StringToLong());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(short.class)), new StringToShort());

        // for less boxing invocation
        // it will box, but not in the exported product
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Boolean.class)), new StringToBooleanWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Byte.class)), new StringToByteWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Double.class)), new StringToDoubleWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Float.class)), new StringToFloatWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Integer.class)), new StringToIntegerWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Long.class)), new StringToLongWrapper());
        registerTransformation(new ClassPair(Java.Class(String.class), Java.Class(Short.class)), new StringToShortWrapper());

        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(boolean.class)), new NumberToBoolean());

        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(byte.class)), new NumberToByte());
        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(short.class)), new NumberToShort());
        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(int.class)), new NumberToInt());
        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(long.class)), new NumberToLong());
        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(double.class)), new NumberToDouble());
        registerTransformation(new ClassPair(Java.Class(Number.class), Java.Class(float.class)), new NumberToFloat());

        registerTransformation(new ClassPair(Java.Class(int.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new ClassPair(Java.Class(short.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new ClassPair(Java.Class(long.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new ClassPair(Java.Class(double.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new ClassPair(Java.Class(byte.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new ClassPair(Java.Class(float.class), Java.Class(boolean.class)), new NumberToBoolean());

        registerTransformation(new ClassPair(Java.Class(Object.class), Java.Class(boolean.class)), new ObjectToBoolean());
        registerTransformation(new ClassPair(Java.Class(Object.class), Java.Class(String.class)), new ObjectToString());
    }

    public static ObjectTransformation getTransformation(IClass source, IClass target) {
        for (Map.Entry<ClassPair, ObjectTransformation> entry : transformationMap.entrySet()) {
            if (BCHelper.isAssignableFrom(entry.getKey().getSource(), source, new HashSet<>()) &&
                BCHelper.isAssignableFrom(target, entry.getKey().getTarget(), new HashSet<>())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void registerTransformation(ClassPair pair, ObjectTransformation objectTransformation) {
        transformationMap.put(pair, objectTransformation);
    }

    @Contract(pure = true)
    public static Reference followWidestConversion(Reference target, Reference compare) {
        IClass higher = BCHelper.getPrioritized(target.getType(), compare.getType());
        return cast(target, higher);
    }

    public static boolean isTransformationPossible(IClass source, IClass target) {
        ObjectTransformation objectTransformation = getTransformation(source, target);
        if (objectTransformation != null) {
            return true;
        }
        if (BCHelper.isWrapper(source)) {
            ObjectTransformation primitiveSourceTransformation = getTransformation(BCHelper.wrapperToPrimitive(source), target);
            return primitiveSourceTransformation != null && isTransformationPossible(source, BCHelper.wrapperToPrimitive(source));
        }
        if (BCHelper.isWrapper(target)) {
            ObjectTransformation primitiveTargetTransformation = getTransformation(source, BCHelper.wrapperToPrimitive(target));
            return primitiveTargetTransformation != null && isTransformationPossible(BCHelper.wrapperToPrimitive(source), target);
        }
        if (BCHelper.isWrapper(source) && BCHelper.isWrapper(target)) {
            return isTransformationPossible(source, BCHelper.wrapperToPrimitive(source)) &&
                    isTransformationPossible(BCHelper.wrapperToPrimitive(source), BCHelper.wrapperToPrimitive(target)) &&
                    isTransformationPossible(BCHelper.wrapperToPrimitive(target), target);
        }
        if (BCHelper.isPrimitive(source)) {
            ObjectTransformation wrapperSourceTransformation = getTransformation(BCHelper.primitiveToWrapper(source), target);
            return wrapperSourceTransformation != null && isTransformationPossible(BCHelper.primitiveToWrapper(source), source);
        }
        if (BCHelper.isPrimitive(target)) {
            ObjectTransformation wrapperTargetTransformation = getTransformation(source, BCHelper.primitiveToWrapper(target));
            return wrapperTargetTransformation != null && isTransformationPossible(BCHelper.primitiveToWrapper(target), target);
        }
        return false;
    }

    @Contract(pure = true)
    public static Reference cast(Reference target, IClass type) {
        if (BCHelper.isAssignableFrom(type, target.getType(), new HashSet<>()) ||
                type.getName().equals("void")) {
            return target;
        }

        if (Java.Class(Enum.class).isAssignableFrom(type)) {
            IClass targetType;
            if (BCHelper.isPrimitive(target.getType())) {
                targetType = BCHelper.primitiveToWrapper(target.getType());
            } else {
                targetType = target.getType();
            }
            if (Java.Class(String.class).isAssignableFrom(targetType)) {
                StringToEnum stringToEnum = new StringToEnum(type);
                return stringToEnum.transform(target);
            }
            if (Java.Class(Number.class).isAssignableFrom(targetType)) {
                NumberToEnum numberToEnum = new NumberToEnum(type);
                return numberToEnum.transform(target);
            }
        }

        ObjectTransformation objectTransformation = getTransformation(target.getType(), type);
        if (objectTransformation != null) {
            return objectTransformation.transform(target);
        }

        // Wrapper A to Primitive A > Primitive A to B
        if (BCHelper.isWrapper(target.getType())) {
            ObjectTransformation primitiveSourceTransformation = getTransformation(BCHelper.wrapperToPrimitive(target.getType()), type);
            if (primitiveSourceTransformation != null) {
                return primitiveSourceTransformation.transform(cast(target, BCHelper.wrapperToPrimitive(target.getType())));
            }
        }

        // A to Primitive B > Primitive B to Wrapper B
        if (BCHelper.isWrapper(type)) {
            ObjectTransformation primitiveTargetTransformation = getTransformation(target.getType(), BCHelper.wrapperToPrimitive(type));
            if (primitiveTargetTransformation != null) {
                return cast(primitiveTargetTransformation.transform(target), type);
            }
        }

        if (BCHelper.isPrimitive(target.getType())) {
            ObjectTransformation wrapperSourceTransformation = getTransformation(BCHelper.primitiveToWrapper(target.getType()), type);
            if (wrapperSourceTransformation != null) {
                return wrapperSourceTransformation.transform(cast(target, BCHelper.primitiveToWrapper(target.getType())));
            }
        }

        if (BCHelper.isPrimitive(type)) {
            ObjectTransformation wrapperTargetTransformation = getTransformation(target.getType(), BCHelper.primitiveToWrapper(type));
            if (wrapperTargetTransformation != null) {
                return cast(wrapperTargetTransformation.transform(target), type);
            }
        }

        // Wrapper to Wrapper conversion
        // Wrapper A to Primitive A > Primitive A to Primitive B > Primitive B to Wrapper B
        // Example case: Double to Integer
        // Double > double > int > Integer
        if (BCHelper.isWrapper(target.getType()) && BCHelper.isWrapper(type)) {
            return cast(cast(cast(target, BCHelper.wrapperToPrimitive(target.getType())), BCHelper.wrapperToPrimitive(type)), type);
        }

        if (BCHelper.isPrimitive(target.getType()) || BCHelper.isPrimitive(type)) throw new ClassCastException("cannot cast "+target.getType().getName()+" to "+type.getName());
        return new Reference(type) {
            @Override
            public void writeByteCode() {
                final MethodContext context = MethodContext.getContext();
                target.writeByteCode();
                context.pushNode(new TypeInsnNode(Opcodes.CHECKCAST, BCHelper.getClassPath(type)));
            }

            @Override
            public void writeSourceCode() {
                SourceCode code = SourceCode.getContext();
                StringBuilder line = code.getLine();
                line.append('(');
                line.append(code.simplifyType(type));
                line.append(") ");
                target.writeSourceCode();
            }
        };
    }

}
