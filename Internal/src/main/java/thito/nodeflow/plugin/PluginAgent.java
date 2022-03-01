package thito.nodeflow.plugin;

import org.objectweb.asm.*;

public class PluginAgent {
    private Plugin plugin;
    private int api = Opcodes.V17;
    public byte[] rewriteClass(byte[] byteCode) {
        ClassReader classReader = new ClassReader(byteCode);
        ClassWriter classWriter = new ClassWriter(0);
        classReader.accept(new ClassVisitor(api, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                };
            }
        }, 0);
        return classWriter.toByteArray();
    }
}
