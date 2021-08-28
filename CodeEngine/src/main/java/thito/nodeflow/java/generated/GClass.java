package thito.nodeflow.java.generated;

import thito.nodeflow.java.*;

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
    private String pkg;

    public GClass(Context context, String pkg, String name, IClass declaringClass) {
        super(name, declaringClass);
        this.context = context;
        this.pkg = pkg;
    }

    @Override
    public String getSimpleName() {
        return super.getName();
    }

    @Override
    public String getName() {
        StringBuilder name = new StringBuilder();
        IClass target = this;
        while (target != null) {
            name.insert(0, "." + target.getSimpleName());
            target = target.getDeclaringClass();
        }
        String pkg = getPackageName();
        if (pkg == null || pkg.isEmpty()) {
            return name.substring(1);
        } else {
            name.insert(0, getPackageName());
        }
        return name.toString();
    }

    @Override
    public Annotated[] getAnnotations() {
        return annotatedList.toArray(new Annotated[0]);
    }

    @Override
    public IClass getComponentType() {
        return null;
    }

    public GClass annotated(Annotated annotated) {
        annotatedList.add(annotated);
        return this;
    }

    public List<Annotated> getAnnotatedList() {
        return annotatedList;
    }

    @Override
    public String getPackageName() {
        return pkg;
    }

    public GMethod declareStaticInitializer() {
        return declareMethod("<clinit>").setModifier(Modifier.STATIC);
    }

    public GClass declareClass(String name) {
        GClass clazz = context.declareClass(getPackageName(), name, this);
        declaredClasses.add(clazz);
        return clazz;
    }

    public GField declareField(String name, IClass type) {
        GField field = new GField(name, this, type);
        if (declaredFields.contains(field)) throw new IllegalArgumentException("field already exist");
        declaredFields.add(field);
        return field;
    }

    public GConstructor declaredConstructor(IClass...parameterTypes) {
        GConstructor constructor = new GConstructor(this);
        constructor.setParameterTypes(parameterTypes);
        if (declaredConstructors.contains(constructor)) throw new IllegalArgumentException("constructor already exist");
        declaredConstructors.add(constructor);
        return constructor;
    }

    public GMethod declareMethod(String name, IClass... parameterTypes) {
        GMethod method = new GMethod(name, this);
        method.setParameterTypes(parameterTypes);
        if (declaredMethods.contains(method)) throw new IllegalArgumentException("method already exist");
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
    public GClass[] getDeclaredClasses() {
        return declaredClasses.toArray(new GClass[0]);
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
