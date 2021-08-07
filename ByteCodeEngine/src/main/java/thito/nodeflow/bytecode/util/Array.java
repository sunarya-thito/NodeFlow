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
    }
    public static Reference getLength(Object array) {
        return null;
    }
    public static Reference newInstance(IClass type, Object...dimensions) {
        if (dimensions.length == 1) {
            MethodContext context = MethodContext.getContext();
            BCHelper.writeToContext(Java.Class(int.class), dimensions[0]);
            LField field = context.getAccessor().createLocal(Java.ArrayClass(type, dimensions.length));
            field.set(new Reference(field.getType()) {
                @Override
                public void write() {

                }
            });
            return field.get();
        }
        return null;
    }
}
