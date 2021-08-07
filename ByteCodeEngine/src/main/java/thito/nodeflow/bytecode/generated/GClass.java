package thito.nodeflow.bytecode.generated;

import thito.nodeflow.bytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class GClass extends AbstractClass {
    private IClass superClass = Java.Class(Object.class);
    private List<IClass> interfaces = new ArrayList<>();
    private List<IField> declaredFields = new ArrayList<>();
    private List<IMethod> declaredMethods = new ArrayList<>();
    private List<IConstructor> declaredConstructors = new ArrayList<>();
    private List<IClass> declaredClasses = new ArrayList<>();
    private List<Annotated> annotatedList = new ArrayList<>();
    private Context context;

    public GClass(Context context, String name, IClass declaringClass) {
        super(name, declaringClass);
        this.context = context;
    }

    @Override
    public boolean isAssignableFrom(IClass type) {
        if (equals(type)) return true;
        if (superClass != null) {
            if (superClass.isAssignableFrom(type)) {
                return true;
            }
        }
        for (IClass i : interfaces) {
            if (i.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IClass getComponentType() {
        return null;
    }

    public GClass annotated(String name, Object value) {
        annotatedList.add(new Annotated(name, value));
        return this;
    }

    public List<Annotated> getAnnotatedList() {
        return annotatedList;
    }

    public GClass declareClass(String name) {
        GClass clazz = context.declareClass(name, this);
        declaredClasses.add(clazz);
        return clazz;
    }

    public GField declareField(String name, IClass type) {
        GField field = new GField(name, this, type);
        declaredFields.add(field);
        return field;
    }

    public GConstructor declaredConstructor(IClass...parameterTypes) {
        GConstructor constructor = new GConstructor(this);
        constructor.setParameterTypes(parameterTypes);
        declaredConstructors.add(constructor);
        return constructor;
    }

    public GMethod declareMethod(String name, IClass... parameterTypes) {
        GMethod method = new GMethod(name, this);
        method.setParameterTypes(parameterTypes);
        declaredMethods.add(method);
        return method;
    }

    public GClass setInterfaces(IClass... interfaces) {
        this.interfaces.clear();
        this.interfaces.addAll(Arrays.asList(interfaces));
        return this;
    }

    public GClass setSuperClass(IClass superClass) {
        this.superClass = superClass;
        return this;
    }

    public List<IClass> getInterfaceList() {
        return interfaces;
    }

    @Override
    public IClass[] getInterfaces() {
        return interfaces.toArray(new IClass[0]);
    }

    @Override
    public IClass getSuperClass() {
        return superClass;
    }

    public GClass setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    @Override
    public IField[] getFields() {
        List<IField> fields = new ArrayList<>(declaredFields);
        return fields.stream().distinct().toArray(IField[]::new);
    }

    @Override
    public GField[] getDeclaredFields() {
        return declaredFields.toArray(new GField[0]);
    }

    @Override
    public IMethod[] getMethods() {
        List<IMethod> methods = new ArrayList<>(declaredMethods);
        return methods.stream().distinct().toArray(IMethod[]::new);
    }

    @Override
    public GMethod[] getDeclaredMethods() {
        return declaredMethods.toArray(new GMethod[0]);
    }

    @Override
    public IConstructor[] getConstructors() {
        List<IConstructor> constructors = new ArrayList<>(declaredConstructors);
        return constructors.stream().distinct().toArray(IConstructor[]::new);
    }

    @Override
    public GConstructor[] getDeclaredConstructors() {
        return declaredConstructors.toArray(new GConstructor[0]);
    }

    @Override
    public IClass[] getDeclaredClasses() {
        return declaredClasses.toArray(new IClass[0]);
    }

    @Override
    public IClass[] getClasses() {
        Set<IClass> classes = new HashSet<>(declaredClasses);
        IClass superClass = getSuperClass();
        if (superClass != null) {
            classes.addAll(Arrays.asList(superClass.getClasses()));
        }
        IClass[] interfaces = getInterfaces();
        if (interfaces != null) {
            for (IClass i : interfaces) {
                classes.addAll(Arrays.asList(i.getClasses()));
            }
        }
        return classes.toArray(new IClass[0]);
    }
}
