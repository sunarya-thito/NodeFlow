package thito.nodeflow.library;

import java.util.*;

public class ClassMember extends Member {
    protected String packageName;
    protected TypeCode superclass;
    protected TypeCode[] interfaces;
    protected ClassType type;
    protected List<FieldMember> fields = new ArrayList<>();
    protected List<MethodMember> methods = new ArrayList<>();
    protected List<ConstructorMember> constructors = new ArrayList<>();

    public ClassMember(String code) {
        JavaTokenizer tokenizer = new JavaTokenizer(code);
        annotations = tokenizer.eatAnnotations();
        modifiers = tokenizer.eatModifiers();
        type = tokenizer.eatClassType();
        name = tokenizer.eatMemberName();
        generics = tokenizer.eatGenericDeclarations();
        if (tokenizer.eatKeyword("extends") != null) {
            superclass = tokenizer.eatType();
        }
        if (tokenizer.eatKeyword("implements") != null) {
            ArrayList<TypeCode> interfaces = new ArrayList<>();
            TypeCode inter;
            while ((inter = tokenizer.eatType()) != null) {
                interfaces.add(inter);
            }
            this.interfaces = interfaces.toArray(new TypeCode[0]);
        }
    }

    public String getFullName() {
        String name = getName();
        if (packageName != null && !packageName.trim().isEmpty()) {
            name = packageName.trim() + "." + name;
        }
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public TypeCode getSuperclass() {
        return superclass;
    }

    public void setSuperclass(TypeCode superclass) {
        this.superclass = superclass;
    }

    public TypeCode[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(TypeCode[] interfaces) {
        this.interfaces = interfaces;
    }

    public ClassType getType() {
        return type;
    }

    public void setType(ClassType type) {
        this.type = type;
    }

    public List<FieldMember> getFields() {
        return fields;
    }

    public void setFields(List<FieldMember> fields) {
        this.fields = fields;
    }

    public List<MethodMember> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodMember> methods) {
        this.methods = methods;
    }

    public List<ConstructorMember> getConstructors() {
        return constructors;
    }

    public void setConstructors(List<ConstructorMember> constructors) {
        this.constructors = constructors;
    }

    public void add(Member member) {
        if (member instanceof MethodMember) {
            methods.add((MethodMember) member);
        } else if (member instanceof FieldMember) {
            fields.add((FieldMember) member);
        } else if (member instanceof ConstructorMember) {
            constructors.add((ConstructorMember) member);
        }
    }
}
