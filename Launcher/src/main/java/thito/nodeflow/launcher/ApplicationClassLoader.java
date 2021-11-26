package thito.nodeflow.launcher;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ApplicationClassLoader extends URLClassLoader {
    public ApplicationClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void initialize(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName("thito.nodeflow.Bootstrap", false, this);
        Constructor<?> constructor = clazz.getConstructors()[0];
        constructor.setAccessible(true);
        constructor.newInstance(new Object[]{args});
    }

    private Map<String, Integer> fieldThreadMap = new HashMap<>();

    private int getThreadIdFromDescriptor(String descriptor) {
        return switch (descriptor) {
            case "thito/nodeflow/annotation/BGThread" -> 1;
            case "thito/nodeflow/annotation/IOThread" -> 2;
            case "thito/nodeflow/annotation/UIThread" -> 3;
            default -> 0;
        };
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        InputStream inputStream = getResourceAsStream(name.replace('.', '/').concat(".class"));
        if (inputStream != null) {
            try {
                byte[] classBytes = inputStream.readAllBytes();
                ClassReader classReader = new ClassReader(classBytes);
                ClassWriter classWriter = new ClassWriter(classReader, 0);
                classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {

                    @Override
                    public FieldVisitor visitField(int access, String name, String fieldDescriptor, String signature, Object value) {
                        return new FieldVisitor(api, super.visitField(access, name, fieldDescriptor, signature, value)) {
                            @Override
                            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                int threadID = getThreadIdFromDescriptor(descriptor);
                                if (threadID != 0) {
                                    fieldThreadMap.put(fieldDescriptor+"#"+name, threadID);
                                }
                                return super.visitAnnotation(descriptor, visible);
                            }

                        };
                    }

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            private int threadID;

                            @Override
                            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                Integer threadID = fieldThreadMap.get(owner+"#"+name);
                                if (threadID != null) {
                                    writeThreadCheckInvocation(threadID);
                                }
                                super.visitFieldInsn(opcode, owner, name, descriptor);
                            }

                            @Override
                            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                int threadID = getThreadIdFromDescriptor(descriptor);
                                if (threadID != 0) this.threadID = threadID;
                                return super.visitAnnotation(descriptor, visible);
                            }

                            @Override
                            public void visitCode() {
                                super.visitCode();
                                writeThreadCheckInvocation(threadID);
                            }

                            private void writeThreadCheckInvocation(int threadID) {
                                switch (threadID) {
                                    case 1 -> // Background Thread
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "thito/nodeflow/task/TaskThread", "BG", "()Lthito/nodeflow/task/TaskThread", false);
                                    case 2 -> // IO Thread
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "thito/nodeflow/task/TaskThread", "IO", "()Lthito/nodeflow/task/TaskThread", false);
                                    case 3 -> // UI Thread
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "thito/nodeflow/task/TaskThread", "UI", "()Lthito/nodeflow/task/TaskThread", false);
                                }
                                if (threadID != 0) {
                                    super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "thito/nodeflow/task/TaskThread", "checkThread", "()V", true);
                                }
                            }

                        };
                    }
                }, 0);
                classBytes = classWriter.toByteArray();
                return defineClass(name, classBytes, 0, classBytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new ClassNotFoundException(name);
    }
}
