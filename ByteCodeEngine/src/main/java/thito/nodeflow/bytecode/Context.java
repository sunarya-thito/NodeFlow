package thito.nodeflow.bytecode;

import org.objectweb.asm.*;
import thito.nodeflow.bytecode.generated.*;
import thito.nodeflow.bytecode.generated.body.*;

import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.util.*;

public class Context implements AutoCloseable {
    private static ThreadLocal<Context> context = new ThreadLocal<>();

    public static Context open() {
        Context ctx = context.get();
        if (ctx != null) throw new IllegalStateException("already open");
        ctx = new Context();
        context.set(ctx);
        return ctx;
    }

    public static Context getContext() {
        Context ctx = context.get();
        if (ctx == null) throw new IllegalStateException("no context");
        return ctx;
    }

    private Set<GClass> generatedClasses = new HashSet<>();
    protected Map<Class<?>, IClass> cachedClassMap = new HashMap<>();

    public GClass declareClass(String name, IClass declaringClass) {
        GClass clazz = new GClass(this, name, declaringClass);
        generatedClasses.add(clazz);
        return clazz;
    }

    public GClass declareClass(String name) {
        return declareClass(name, null);
    }

    public GClass getClass(String name) {
        return generatedClasses.stream().filter(gClass -> gClass.getName().equals(name)).findAny().orElse(null);
    }

    public byte[] writeClass(GClass clazz) {
        ClassWriter writer = new ClassWriter(Opcodes.ASM4);
        for (GClass gClass : generatedClasses) {
            GConstructor[] declaredConstructors = gClass.getDeclaredConstructors();
            if (declaredConstructors.length == 0) {
                GConstructor constructor = new GConstructor(gClass);
                constructor.setModifier(Modifier.PUBLIC);
                constructor.setBody(body -> {
                    body.Super().invokeVoid();
                });

            } else {
                for (GConstructor constructor : declaredConstructors) {

                }
            }
            for (GMethod method : gClass.getDeclaredMethods()) {

            }
            for (GField field : gClass.getDeclaredFields()) {

            }
        }
        return writer.toByteArray();
    }

    private void writeConstructor(MethodVisitor visitor, GConstructor constructor) {
        ConstructorBodyAccessor bodyAccessor = new ConstructorBodyAccessor(constructor);
        try (MethodContext methodContext = MethodContext.open(bodyAccessor)) {

        }
    }

    private void writeMethod(MethodVisitor visitor, GMethod method) {

    }

    private void writeField(ClassVisitor visitor, GField field) {
        visitor.visitField(field.getModifiers(), field.getName(), BCHelper.getDescriptor(field.getType()), null, null);

    }

    public void close() {
        if (context.get() == this) {
            context.set(null);
        } else throw new IllegalStateException("already closed");
    }
}
