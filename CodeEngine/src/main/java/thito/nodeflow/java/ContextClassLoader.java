package thito.nodeflow.java;

import org.objectweb.asm.*;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.known.*;

public class ContextClassLoader extends ClassLoader {
    public Class<?> loadClass(IClass iClass) {
        if (iClass instanceof GClass) {
            byte[] bytecode = ((GClass) iClass).getContext().writeClassByteCode((GClass) iClass, Opcodes.V1_8);
            return defineClass(iClass.getName(), bytecode, 0, bytecode.length);
        } else if (iClass instanceof KClass) {
            return ((KClass) iClass).getWrapped();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
