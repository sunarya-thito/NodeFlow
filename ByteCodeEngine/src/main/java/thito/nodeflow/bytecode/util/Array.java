package thito.nodeflow.bytecode.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;

public class Array {
    public static Reference get(Object array, Object index) {
        IClass finalComponent = BCHelper.getType(array).getComponentType();
        return new Reference(finalComponent) {
            @Override
            public void write() {
                BCHelper.writeToContext(Java.Class(Object.class), array);
                BCHelper.writeToContext(Java.Class(int.class), index);
                MethodContext.getContext().pushNode(new InsnNode(Opcodes.AALOAD));
            }
        };
    }
    public static void set(Object array, Object index, Object value) {
        MethodContext context = MethodContext.getContext();
        BCHelper.writeToContext(Java.Class(Object.class), array);
        BCHelper.writeToContext(Java.Class(int.class), index);
        BCHelper.writeToContext(BCHelper.getType(array).getComponentType(), value);
        context.pushNode(new InsnNode(Opcodes.AASTORE));
    }
    public static Reference getLength(Object array) {
        return null;
    }
    public static Reference newInstance(IClass type, Object...dimensions) {
        if (dimensions.length == 1) {
            MethodContext context = MethodContext.getContext();
            LField field = context.getAccessor().createLocal(Java.ArrayClass(type, 1));
            field.set(new Reference(field.getType()) {
                @Override
                public void write() {
                    BCHelper.writeToContext(Java.Class(int.class), dimensions[0]);
                    context.pushNode(new TypeInsnNode(Opcodes.ANEWARRAY, BCHelper.getClassPath(type)));
                }
            });
            return field.get();
        }
        MethodContext context = MethodContext.getContext();
        LField field = context.getAccessor().createLocal(Java.ArrayClass(type, dimensions.length));
        field.set(new Reference(field.getType()) {
            @Override
            public void write() {
                for (Object dim : dimensions) {
                    BCHelper.writeToContext(Java.Class(int.class), dim);
                }
                context.pushNode(new MultiANewArrayInsnNode(BCHelper.getArrayPrefix(dimensions.length)+BCHelper.getDescriptor(type), dimensions.length));
            }
        });
        return field.get();
    }
}
