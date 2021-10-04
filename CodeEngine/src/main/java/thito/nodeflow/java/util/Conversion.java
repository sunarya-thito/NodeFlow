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

    public static Map<ClassPair, ObjectTransformation> getTransformationMap() {
        return Context.getContext().getTransformationMap();
    }

    public static ObjectTransformation getTransformation(IClass source, IClass target) {
        for (Map.Entry<ClassPair, ObjectTransformation> entry : getTransformationMap().entrySet()) {
            if (BCHelper.isAssignableFrom(entry.getKey().getSource(), source, new HashSet<>()) &&
                BCHelper.isAssignableFrom(target, entry.getKey().getTarget(), new HashSet<>())) {
                return entry.getValue();
            }
        }
        return null;
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

//        if (Java.Class(Enum.class).isAssignableFrom(type)) {
        if (BCHelper.isAssignableFrom(Java.Class(Enum.class), type, new HashSet<>())) {
            IClass targetType;
            if (BCHelper.isPrimitive(target.getType())) {
                targetType = BCHelper.primitiveToWrapper(target.getType());
            } else {
                targetType = target.getType();
            }
//            if (Java.Class(String.class).isAssignableFrom(targetType)) {
            if (BCHelper.isAssignableFrom(Java.Class(String.class), targetType, new HashSet<>())) {
                StringToEnum stringToEnum = new StringToEnum(type);
                return stringToEnum.transform(target);
            }
//            if (Java.Class(Number.class).isAssignableFrom(targetType)) {
            if (BCHelper.isAssignableFrom(Java.Class(Number.class), targetType, new HashSet<>())) {
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
