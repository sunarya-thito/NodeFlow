package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.generated.body.*;
import thito.nodeflow.java.transform.*;
import thito.nodeflow.java.util.*;

import java.lang.reflect.Array;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

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
        ctx.initialize();
        return ctx;
    }

    public static Context getContext() {
        Context ctx = context.get();
        if (ctx == null) throw new IllegalStateException("no context");
        return ctx;
    }

    private Set<GClass> generatedClasses = new HashSet<>();
    protected Map<Class<?>, IClass> cachedClassMap = new ConcurrentHashMap<>();
    private Map<Conversion.ClassPair, ObjectTransformation> transformationMap = new HashMap<>();
    public Map<Conversion.ClassPair, ObjectTransformation> getTransformationMap() {
        return transformationMap;
    }

    private void initialize() {
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(char.class)), new ByteToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(double.class)), new ByteToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(float.class)), new ByteToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(int.class)), new ByteToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(long.class)), new ByteToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(short.class)), new ByteToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(byte.class)), new CharToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(double.class)), new CharToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(float.class)), new CharToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(int.class)), new CharToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(long.class)), new CharToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(short.class)), new CharToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(byte.class)), new DoubleToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(char.class)), new DoubleToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(float.class)), new DoubleToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(int.class)), new DoubleToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(long.class)), new DoubleToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(short.class)), new DoubleToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(byte.class)), new FloatToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(char.class)), new FloatToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(double.class)), new FloatToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(int.class)), new FloatToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(long.class)), new FloatToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(short.class)), new FloatToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(byte.class)), new IntToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(char.class)), new IntToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(double.class)), new IntToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(float.class)), new IntToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(long.class)), new IntToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(short.class)), new IntToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(byte.class)), new ShortToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(char.class)), new ShortToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(double.class)), new ShortToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(float.class)), new ShortToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(int.class)), new ShortToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(long.class)), new ShortToLong());

        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(byte.class)), new LongToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(char.class)), new LongToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(double.class)), new LongToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(float.class)), new LongToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(int.class)), new LongToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(short.class)), new LongToShort());

        registerTransformation(new Conversion.ClassPair(Java.Class(Long.class), Java.Class(long.class)), new UnboxingLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(Double.class), Java.Class(double.class)), new UnboxingDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(Integer.class), Java.Class(int.class)), new UnboxingInteger());
        registerTransformation(new Conversion.ClassPair(Java.Class(Float.class), Java.Class(float.class)), new UnboxingFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(Character.class), Java.Class(char.class)), new UnboxingCharacter());
        registerTransformation(new Conversion.ClassPair(Java.Class(Short.class), Java.Class(short.class)), new UnboxingShort());
        registerTransformation(new Conversion.ClassPair(Java.Class(Byte.class), Java.Class(byte.class)), new UnboxingByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(Boolean.class), Java.Class(boolean.class)), new UnboxingBoolean());

        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(Long.class)), new BoxingLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(Double.class)), new BoxingDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(Integer.class)), new BoxingInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(Float.class)), new BoxingFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(char.class), Java.Class(Character.class)), new BoxingChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(Short.class)), new BoxingShort());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(Byte.class)), new BoxingByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(boolean.class), Java.Class(Boolean.class)), new BoxingBoolean());

        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(boolean.class)), new StringToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(byte.class)), new StringToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(char.class)), new StringToChar());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(double.class)), new StringToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(float.class)), new StringToFloat());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(int.class)), new StringToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(long.class)), new StringToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(short.class)), new StringToShort());

        // for less boxing invocation
        // it will box, but not in the exported product
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Boolean.class)), new StringToBooleanWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Byte.class)), new StringToByteWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Double.class)), new StringToDoubleWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Float.class)), new StringToFloatWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Integer.class)), new StringToIntegerWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Long.class)), new StringToLongWrapper());
        registerTransformation(new Conversion.ClassPair(Java.Class(String.class), Java.Class(Short.class)), new StringToShortWrapper());

        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(boolean.class)), new NumberToBoolean());

        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(byte.class)), new NumberToByte());
        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(short.class)), new NumberToShort());
        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(int.class)), new NumberToInt());
        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(long.class)), new NumberToLong());
        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(double.class)), new NumberToDouble());
        registerTransformation(new Conversion.ClassPair(Java.Class(Number.class), Java.Class(float.class)), new NumberToFloat());

        registerTransformation(new Conversion.ClassPair(Java.Class(int.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(short.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(long.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(double.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(byte.class), Java.Class(boolean.class)), new NumberToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(float.class), Java.Class(boolean.class)), new NumberToBoolean());

        registerTransformation(new Conversion.ClassPair(Java.Class(Object.class), Java.Class(boolean.class)), new ObjectToBoolean());
        registerTransformation(new Conversion.ClassPair(Java.Class(Object.class), Java.Class(String.class)), new ObjectToString());
    }

    public void registerTransformation(Conversion.ClassPair pair, ObjectTransformation objectTransformation) {
        transformationMap.put(pair, objectTransformation);
    }

    public GClass declareClass(String pkg, String name, IClass declaringClass) {
        if (declaringClass != null && !isSamePackage(pkg, declaringClass.getPackageName())) throw new IllegalArgumentException("wrong package for declaring class "+pkg+" != "+declaringClass.getPackageName());
        String nameNoPackage = null;
        String canonName = null;
        if (declaringClass != null) {
            nameNoPackage = declaringClass.getName();
            if (pkg != null) {
                int index = nameNoPackage.indexOf(pkg + ".");
                nameNoPackage = nameNoPackage.substring(index);
            }
            canonName = declaringClass.getCanonicalName();
        }
        GClass clazz = new GClass(this, pkg, nameNoPackage == null ? name : nameNoPackage + "$" + name, canonName == null ? name : canonName + "." + name, declaringClass);
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
                sourceCode.getLines().add(0, new StringBuilder("import ").append(type.getCanonicalName()).append(';'));
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
            sourceCode.getLine().append(sourceCode.simplifyType(superClass));
        }
        if (interfaces != null && interfaces.length > 0) {
            sourceCode.getLine().append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i != 0) sourceCode.getLine().append(", ");
                sourceCode.getLine().append(sourceCode.simplifyType(interfaces[i]));
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
            code.getLine().append("@").append(code.simplifyType(annotated.getType()));
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
                            code.getLine().append(element);
                        }
                    } else if (val instanceof Enum) {
                        code.getLine().append(code.simplifyType(Java.Class(((Enum<?>) val).getDeclaringClass())))
                                .append('.')
                                .append(((Enum<?>) val).name());
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
        code.getLine().append(code.simplifyType(field.getType()))
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
            code.getLine().append(code.simplifyType(paramTypes[i]))
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
                code.getLine().append(code.simplifyType(throwsClass[i]));
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
                .append(code.simplifyType(method.getReturnType()))
                .append(' ')
                .append(method.getName())
                .append('(');
        IClass[] paramTypes = method.getParameterTypes();
        int paramStartIndex = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramTypes.length; i++) {
            if (i != 0) code.getLine().append(", ");
            code.getLine().append(code.simplifyType(paramTypes[i]))
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
                code.getLine().append(code.simplifyType(throwsClass[i]));
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

    public byte[] writeClassByteCode(GClass gClass, int javaVersion) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(javaVersion, gClass.getModifiers(), gClass.getName(), null, BCHelper.getClassPath(gClass.getSuperClass()), Arrays.stream(gClass.getInterfaces()).map(BCHelper::getClassPath).toArray(String[]::new));
        writeAnnotationByteCode(gClass, null, null, writer);
        GConstructor[] declaredConstructors = gClass.getDeclaredConstructors();
        if (declaredConstructors.length == 0) {
            GConstructor constructor = new GConstructor(gClass);
            constructor.setModifier(Modifier.PUBLIC);
            constructor.setBody(body -> {
                body.Super().invoke();
            });
            MethodVisitor visitor = writer.visitMethod(constructor.getModifiers(),
                    "<init>",
                    BCHelper.getMethodDescriptor(Java.Class(void.class), constructor.getParameterTypes()),
                    null,
                    constructor.getThrowsClasses().stream().map(BCHelper::getClassPath).toArray(String[]::new));
            writeAnnotationByteCode(constructor, visitor, null, null);
            visitor.visitCode();
            writeConstructorByteCode(visitor, constructor);
            visitor.visitEnd();
        } else {
            for (GConstructor constructor : declaredConstructors) {
                MethodVisitor visitor = writer.visitMethod(constructor.getModifiers(),
                        "<init>",
                        BCHelper.getMethodDescriptor(Java.Class(void.class), constructor.getParameterTypes()),
                        null,
                        constructor.getThrowsClasses().stream().map(BCHelper::getClassPath).toArray(String[]::new));
                writeAnnotationByteCode(constructor, visitor, null, null);
                visitor.visitCode();
                writeConstructorByteCode(visitor, constructor);
                visitor.visitEnd();
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
                        } else {
                            arrayVisitor.visit(null, element);
                        }
                    }
                } else if (val instanceof Enum) {
                    annotationVisitor.visitEnum(value.getName(), Type.getDescriptor(val.getClass().getComponentType()), ((Enum<?>) val).name());
                } else {
                    annotationVisitor.visit(value.getName(), val);
                }
            }
        }
    }

    private void writeConstructorByteCode(MethodVisitor visitor, GConstructor constructor) {
        Consumer<ConstructorBodyAccessor> body = constructor.getBody();
        if (body != null) {
            ConstructorBodyAccessor bodyAccessor = new ConstructorBodyAccessor(constructor);
            visitor.visitCode();
            try (MethodContext methodContext = MethodContext.open(bodyAccessor)) {
                body.accept(bodyAccessor);
                methodContext.write(visitor);
            }
            visitor.visitMaxs(-1, -1);
            visitor.visitEnd();
        }
    }

    private void writeMethodByteCode(MethodVisitor visitor, GMethod method) {
        Consumer<MethodBodyAccessor> body = method.getBody();
        if (body != null) {
            MethodBodyAccessor bodyAccessor = new MethodBodyAccessor(method);
            visitor.visitCode();
            try (MethodContext methodContext = MethodContext.open(bodyAccessor)) {
                body.accept(bodyAccessor);
                methodContext.write(visitor);
            }
            visitor.visitMaxs(-1, -1);
            visitor.visitEnd();
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
