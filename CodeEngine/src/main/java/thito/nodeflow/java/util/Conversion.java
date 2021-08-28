package thito.nodeflow.java.util;

import javafx.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.transform.*;

import java.util.*;

public class Conversion {
    private static Map<Pair<IClass, IClass>, ObjectTransformation> transformationMap = new HashMap<>();
    static {
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(char.class)), new ByteToChar());
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(double.class)), new ByteToDouble());
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(float.class)), new ByteToFloat());
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(int.class)), new ByteToInt());
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(long.class)), new ByteToLong());
        registerTransformation(new Pair<>(Java.Class(byte.class), Java.Class(short.class)), new ByteToShort());

        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(byte.class)), new CharToByte());
        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(double.class)), new CharToDouble());
        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(float.class)), new CharToFloat());
        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(int.class)), new CharToInt());
        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(long.class)), new CharToLong());
        registerTransformation(new Pair<>(Java.Class(char.class), Java.Class(short.class)), new CharToShort());

        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(byte.class)), new DoubleToByte());
        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(char.class)), new DoubleToChar());
        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(float.class)), new DoubleToFloat());
        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(int.class)), new DoubleToInt());
        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(long.class)), new DoubleToLong());
        registerTransformation(new Pair<>(Java.Class(double.class), Java.Class(short.class)), new DoubleToShort());

        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(byte.class)), new FloatToByte());
        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(char.class)), new FloatToChar());
        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(double.class)), new FloatToDouble());
        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(int.class)), new FloatToInt());
        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(long.class)), new FloatToLong());
        registerTransformation(new Pair<>(Java.Class(float.class), Java.Class(short.class)), new FloatToShort());

        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(byte.class)), new IntToByte());
        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(char.class)), new IntToChar());
        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(double.class)), new IntToDouble());
        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(float.class)), new IntToFloat());
        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(long.class)), new IntToLong());
        registerTransformation(new Pair<>(Java.Class(int.class), Java.Class(short.class)), new IntToShort());

        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(byte.class)), new ShortToByte());
        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(char.class)), new ShortToChar());
        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(double.class)), new ShortToDouble());
        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(float.class)), new ShortToFloat());
        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(int.class)), new ShortToInt());
        registerTransformation(new Pair<>(Java.Class(short.class), Java.Class(long.class)), new ShortToLong());
    }

    public static void registerTransformation(Pair<IClass, IClass> pair, ObjectTransformation objectTransformation) {
        transformationMap.put(pair, objectTransformation);
    }

    public static Reference followWidestConversion(Reference target, Reference compare) {
        IClass higher = BCHelper.getPrioritized(target.getType(), compare.getType());
        return cast(target, higher);
    }

    public static Reference cast(Reference target, IClass type) {
        if (type.isAssignableFrom(target.getType()) || type.getName().equals("void")) return target;
        Reference unboxed = unbox(target);
        if (unboxed != target) {
            type = BCHelper.wrapperToPrimitive(type);
            if (type != null) {
                Pair<IClass, IClass> key = new Pair<>(unboxed.getType(), type);
                ObjectTransformation objectTransformation = transformationMap.get(key);
                if (objectTransformation != null) {
                    return objectTransformation.transform(target);
                }
            }
        }
        IClass finalType = type;
        return new Reference(finalType) {
            @Override
            public void write() {
                final MethodContext context = MethodContext.getContext();
                context.pushNode(new TypeInsnNode(Opcodes.CHECKCAST, BCHelper.getClassPath(finalType)));
                target.write();
            }

            @Override
            public void writeSourceCode() {
                SourceCode code = SourceCode.getContext();
                StringBuilder line = code.getLine();
                line.append('(');
                line.append(code.generalizeType(finalType));
                line.append(") ");
                target.writeSourceCode();
            }
        };
    }

    public static Reference unbox(Reference target) {
        IClass primitive = BCHelper.wrapperToPrimitive(target.getType());
        if (primitive != null && !primitive.equals(target.getType())) {
            return target.method(primitive.getName()+"Value").invoke(target);
        }
        return target;
    }

    public static Reference box(Reference target) {
        IClass wrapper = BCHelper.primitiveToWrapper(target.getType());
        if (wrapper != null) {
            return wrapper.method("valueOf", target.getType()).invoke(target);
        }
        return target;
    }
}
