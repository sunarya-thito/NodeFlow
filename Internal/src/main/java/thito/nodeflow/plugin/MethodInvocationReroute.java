package thito.nodeflow.plugin;

import org.objectweb.asm.MethodVisitor;

public interface MethodInvocationReroute {
    void visitMethod(MethodVisitor visitor, int opcode, String owner, String name, String descriptor, boolean isInterface);
}
