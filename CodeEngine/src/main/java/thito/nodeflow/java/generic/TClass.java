package thito.nodeflow.java.generic;

import thito.nodeflow.java.*;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TClass extends AbstractClass {
    private IClass superClass;
    private IClass[] interfaces;
    private List<IMethod> declaredMethods = new ArrayList<>();
    private List<IField> declaredFields = new ArrayList<>();
    public TClass(String name, IClass superClass, IClass... interfaces) {
        super(name, null);
        this.superClass = superClass;
        this.interfaces = interfaces.clone();
        add(superClass);
        for (IClass i : interfaces) {
            add(i);
        }
    }

    private void add(IClass type) {
        for (IField field : type.getFields()) {
            if (!declaredFields.contains(field)) {
                declaredFields.add(field);
            }
        }
        for (IMethod method : type.getMethods()) {
            if (!declaredMethods.contains(method)) {
                declaredMethods.add(method);
            }
        }
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public IClass[] getInterfaces() {
        return interfaces;
    }

    @Override
    public IClass getSuperClass() {
        return superClass;
    }

    @Override
    public String getSimpleName() {
        return getName();
    }

    @Override
    public String getCanonicalName() {
        return getName();
    }

    @Override
    public IField[] getFields() {
        return declaredFields.toArray(IField[]::new);
    }

    @Override
    public IField[] getDeclaredFields() {
        return getFields();
    }

    @Override
    public IMethod[] getMethods() {
        return declaredMethods.toArray(IMethod[]::new);
    }

    @Override
    public IMethod[] getDeclaredMethods() {
        return getMethods();
    }

    @Override
    public IConstructor[] getConstructors() {
        return new IConstructor[0];
    }

    @Override
    public IConstructor[] getDeclaredConstructors() {
        return new IConstructor[0];
    }

    @Override
    public IClass[] getDeclaredClasses() {
        return new IClass[0];
    }

    @Override
    public IClass[] getClasses() {
        return new IClass[0];
    }

    @Override
    public IClass getComponentType() {
        return null;
    }

    @Override
    public Annotated[] getAnnotations() {
        return new Annotated[0];
    }
}
