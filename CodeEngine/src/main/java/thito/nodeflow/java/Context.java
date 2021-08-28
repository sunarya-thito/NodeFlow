package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.generated.body.*;
import thito.nodeflow.java.util.*;

import java.lang.reflect.Array;
import java.lang.reflect.*;
import java.util.*;

public class Context implements AutoCloseable {

    private static boolean isSamePackage(String pkgA, String pkgB) {
        if (pkgA == null) {
            return pkgB == null || pkgB.isEmpty();
        }
        if (pkgB == null) {
            return pkgA.isEmpty();
        }
        return Objects.equals(pkgA, pkgB);
    }
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

    public GClass declareClass(String pkg, String name, IClass declaringClass) {
        if (declaringClass != null && !isSamePackage(pkg, declaringClass.getPackageName())) throw new IllegalArgumentException("wrong package for declaring class "+pkg+" != "+declaringClass.getPackageName());
        GClass clazz = new GClass(this, pkg, name, declaringClass);
        generatedClasses.add(clazz);
        return clazz;
    }

    public GClass declareClass(String pkg, String simpleName) {
        return declareClass(pkg, simpleName, null);
    }

    public GClass getClass(String name) {
        return generatedClasses.stream().filter(gClass -> gClass.getName().equals(name)).findAny().orElse(null);
    }

    public String writeClassSourceCode(GClass gClass) {
        if (gClass.getDeclaringClass() != null) throw new IllegalArgumentException("inner class is already included inside its declaring class");
        StringBuilder builder = new StringBuilder();
        try (SourceCode sourceCode = SourceCode.openContext()) {
            _writeClassSourceCode(sourceCode, gClass);
            sourceCode.getImportMap().forEach((simpleName, type) -> {
                String pkg = type.getPackageName();
                if (isSamePackage("java.lang", pkg)) return;
                sourceCode.getLines().add(0, new StringBuilder("import ").append(type.getName()).append(';'));
            });
            String pkg = gClass.getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                sourceCode.getLines().add(0, new StringBuilder("package ").append(pkg).append(';'));
            }
            for (StringBuilder line : sourceCode.getLines()) {
                if (!builder.isEmpty()) builder.append('\n');
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private void _writeClassSourceCode(SourceCode sourceCode, GClass gClass) {
        IClass superClass = gClass.getSuperClass();
        IClass[] interfaces = gClass.getInterfaces();
        String modifier = Modifier.toString(gClass.getModifiers());
        writeAnnotationSourceCode(gClass, sourceCode);
        if (!modifier.isEmpty()) {
            sourceCode.getLine().append(modifier);
            sourceCode.getLine().append(' ');
        }
        sourceCode.getLine().append("class ");
        sourceCode.getLine().append(gClass.getSimpleName());
        if (superClass != null && !superClass.getName().equals("java.lang.Object")) {
            sourceCode.getLine().append(" extends ");
            sourceCode.getLine().append(sourceCode.generalizeType(superClass));
        }
        if (interfaces != null && interfaces.length > 0) {
            sourceCode.getLine().append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i != 0) sourceCode.getLine().append(", ");
                sourceCode.getLine().append(sourceCode.generalizeType(interfaces[i]));
            }
        }
        sourceCode.getLine().append(" {");
        sourceCode.endLine();
        sourceCode.incIndent();
        for (GClass inner : gClass.getDeclaredClasses()) {
            _writeClassSourceCode(sourceCode, inner);
        }
        for (GField field : gClass.getDeclaredFields()) {
            writeAnnotationSourceCode(field, sourceCode);
            writeFieldSourceCode(field, sourceCode);
        }
        for (GConstructor constructor : gClass.getDeclaredConstructors()) {
            writeAnnotationSourceCode(constructor, sourceCode);
            writeConstructorSourceCode(constructor, sourceCode);
        }
        for (GMethod method : gClass.getDeclaredMethods()) {
            writeAnnotationSourceCode(method, sourceCode);
            writeMethodSourceCode(method, sourceCode);
        }
        sourceCode.decIndent();
        sourceCode.getLine().append("}");
        sourceCode.endLine();
    }

    private void writeAnnotationSourceCode(IMember member, SourceCode code) {
        for (Annotated annotated : member.getAnnotations()) {
            code.getLine().append("@").append(code.generalizeType(annotated.getType()));
            List<Annotated.Value> values = annotated.getValues();
            if (!values.isEmpty()) {
                code.getLine().append('(');
                for (int i = 0; i < values.size(); i++) {
                    if (i != 0) code.getLine().append(", ");
                    Annotated.Value value = values.get(i);
                    code.getLine().append(value.getName()).append(" = ");
                    Object val = value.getValue();
                    if (val.getClass().isArray()) {
                        code.getLine().append('{');
                        int length = Array.getLength(val);
                        for (int index = 0; index < length; index++) {
                            Object element = Array.get(val, index);
                            if (element instanceof Enum) {
                                code.getLine().append(code.generalizeType(Java.Class(((Enum<?>) element).getDeclaringClass())))
                                        .append('.')
                                        .append(((Enum<?>) element).name());
                            } else if (element instanceof EnumType.Value) {
                                code.getLine().append(code.generalizeType(((EnumType.Value) element).getType()))
                                        .append('.')
                                        .append(((EnumType.Value) element).name());
                            } else {
                                code.getLine().append(element);
                            }
                        }
                    } else if (val instanceof Enum) {
                        code.getLine().append(code.generalizeType(Java.Class(((Enum<?>) val).getDeclaringClass())))
                                .append('.')
                                .append(((Enum<?>) val).name());
                    } else if (val instanceof EnumType.Value) {
                        code.getLine().append(code.generalizeType(((EnumType.Value) val).getType()))
                                .append('.')
                                .append(((EnumType.Value) val).name());
                    } else {
                        code.getLine().append(val);
                    }
                }
            }
            code.endLine();
        }
    }

    private void writeFieldSourceCode(GField field, SourceCode code) {
        String modifiers = Modifier.toString(field.getModifiers());
        if (!modifiers.isEmpty()) {
            code.getLine().append(modifiers).append(' ');
        }
        code.getLine().append(code.generalizeType(field.getType()))
                .append(' ')
                .append(field.getName()).append(';');
    }

    private void writeConstructorSourceCode(GConstructor method, SourceCode code) {
        String modifiers = Modifier.toString(method.getModifiers());
        if (!modifiers.isEmpty()) {
            code.getLine().append(modifiers).append(' ');
        }
        code.getLine()
                .append(method.getDeclaringClass().getSimpleName())
                .append('(');
        int paramStartIndex = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
        IClass[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i != 0) code.getLine().append(", ");
            code.getLine().append(code.generalizeType(paramTypes[i]))
                    .append(' ')
                    .append("var")
                    .append(paramStartIndex + i);
        }
        code.getLine().append(')');
        IClass[] throwsClass = method.getThrows();
        if (throwsClass != null && throwsClass.length > 0) {
            code.getLine().append(" throws ");
            for (int i = 0; i < throwsClass.length; i++) {
                if (i != 0) code.getLine().append(", ");
                code.getLine().append(code.generalizeType(throwsClass[i]));
            }
        }
        code.getLine().append(" {");
        code.endLine();
        code.incIndent();
        method.getBody().accept(new ConstructorBodyAccessor(method));
        code.decIndent();
        code.getLine().append('}');
        code.endLine();
    }

    private void writeMethodSourceCode(GMethod method, SourceCode code) {
        String modifiers = Modifier.toString(method.getModifiers());
        if (!modifiers.isEmpty()) {
            code.getLine().append(modifiers).append(' ');
        }
        code.getLine()
                .append(code.generalizeType(method.getReturnType()))
                .append(' ')
                .append(method.getName())
                .append('(');
        IClass[] paramTypes = method.getParameterTypes();
        int paramStartIndex = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramTypes.length; i++) {
            if (i != 0) code.getLine().append(", ");
            code.getLine().append(code.generalizeType(paramTypes[i]))
                    .append(' ')
                    .append("var")
                    .append(paramStartIndex + i);
        }
        code.getLine().append(')');
        IClass[] throwsClass = method.getThrows();
        if (throwsClass != null && throwsClass.length > 0) {
            code.getLine().append(" throws ");
            for (int i = 0; i < throwsClass.length; i++) {
                if (i != 0) code.getLine().append(", ");
                code.getLine().append(code.generalizeType(throwsClass[i]));
            }
        }
        code.getLine().append(" {");
        code.endLine();
        code.incIndent();
        method.getBody().accept(new MethodBodyAccessor(method));
        code.decIndent();
        code.getLine().append('}');
        code.endLine();
    }

    public byte[] writeClassByteCode(GClass gClass) {
        ClassWriter writer = new ClassWriter(Opcodes.ASM4);
        writeAnnotationByteCode(gClass, null, null, writer);
        GConstructor[] declaredConstructors = gClass.getDeclaredConstructors();
        if (declaredConstructors.length == 0) {
            GConstructor constructor = new GConstructor(gClass);
            constructor.setModifier(Modifier.PUBLIC);
            constructor.setBody(body -> {
                body.Super().invokeVoid();
            });
            MethodVisitor visitor = writer.visitMethod(constructor.getModifiers(),
                    "<init>",
                    BCHelper.getMethodDescriptor(Java.Class(void.class), constructor.getParameterTypes()),
                    null,
                    constructor.getThrowsClasses().stream().map(BCHelper::getClassPath).toArray(String[]::new));
            writeAnnotationByteCode(constructor, visitor, null, null);
            writeConstructorByteCode(visitor, constructor);
        } else {
            for (GConstructor constructor : declaredConstructors) {
                MethodVisitor visitor = writer.visitMethod(constructor.getModifiers(),
                        "<init>",
                        BCHelper.getMethodDescriptor(Java.Class(void.class), constructor.getParameterTypes()),
                        null,
                        constructor.getThrowsClasses().stream().map(BCHelper::getClassPath).toArray(String[]::new));
                writeAnnotationByteCode(constructor, visitor, null, null);
                writeConstructorByteCode(visitor, constructor);
            }
        }
        for (GMethod method : gClass.getDeclaredMethods()) {
            MethodVisitor visitor = writer.visitMethod(method.getModifiers(),
                    method.getName(),
                    BCHelper.getMethodDescriptor(method.getReturnType(), method.getParameterTypes()),
                    null,
                    method.getThrowsClasses().stream().map(BCHelper::getClassPath).toArray(String[]::new));
            writeAnnotationByteCode(method, visitor, null, null);
            writeMethodByteCode(visitor, method);
        }
        for (GField field : gClass.getDeclaredFields()) {
            writeFieldByteCode(writer, field);
        }
        return writer.toByteArray();
    }

    private void writeAnnotationByteCode(IMember member, MethodVisitor visitor, FieldVisitor fieldVisitor, ClassVisitor classVisitor) {
        for (Annotated annotated : member.getAnnotations()) {
            AnnotationVisitor annotationVisitor = visitor != null ? visitor.visitAnnotation(BCHelper.getClassPath(annotated.getType()), annotated.isVisible()) :
                    fieldVisitor != null ? fieldVisitor.visitAnnotation(BCHelper.getDescriptor(annotated.getType()), annotated.isVisible()) :
                    classVisitor.visitAnnotation(BCHelper.getDescriptor(annotated.getType()), annotated.isVisible());
            for (Annotated.Value value : annotated.getValues()) {
                Object val = value.getValue();
                if (val.getClass().isArray() && !val.getClass().getComponentType().isPrimitive()) {
                    AnnotationVisitor arrayVisitor = annotationVisitor.visitArray(value.getName());
                    int length = Array.getLength(val);
                    for (int i = 0; i < length; i++) {
                        Object element = Array.get(val, i);
                        if (element instanceof Enum) {
                            arrayVisitor.visitEnum(null, Type.getDescriptor(val.getClass().getComponentType()), ((Enum<?>) element).name());
                        } else if (element instanceof EnumType.Value) {
                            arrayVisitor.visitEnum(null, BCHelper.getDescriptor(((EnumType.Value) element).getType()), ((EnumType.Value) element).name());
                        } else {
                            arrayVisitor.visit(null, element);
                        }
                    }
                } else if (val instanceof Enum) {
                    annotationVisitor.visitEnum(value.getName(), Type.getDescriptor(val.getClass().getComponentType()), ((Enum<?>) val).name());
                } else if (val instanceof EnumType.Value) {
                    annotationVisitor.visitEnum(value.getName(), BCHelper.getDescriptor(((EnumType.Value) val).getType()), ((EnumType.Value) val).name());
                } else {
                    annotationVisitor.visit(value.getName(), val);
                }
            }
        }
    }

    private void writeConstructorByteCode(MethodVisitor visitor, GConstructor constructor) {
        ConstructorBodyAccessor bodyAccessor = new ConstructorBodyAccessor(constructor);
        try (MethodContext methodContext = MethodContext.open(bodyAccessor)) {
            methodContext.write(visitor);
        }
    }

    private void writeMethodByteCode(MethodVisitor visitor, GMethod method) {
        MethodBodyAccessor bodyAccessor = new MethodBodyAccessor(method);
        try (MethodContext methodContext = MethodContext.open(bodyAccessor)) {
            methodContext.write(visitor);
        }
    }

    private void writeFieldByteCode(ClassVisitor visitor, GField field) {
        FieldVisitor fieldVisitor = visitor.visitField(field.getModifiers(), field.getName(), BCHelper.getDescriptor(field.getType()), null, null);
        writeAnnotationByteCode(field, null, fieldVisitor, null);
    }

    public void close() {
        if (context.get() == this) {
            context.set(null);
        } else throw new IllegalStateException("already closed");
    }
}