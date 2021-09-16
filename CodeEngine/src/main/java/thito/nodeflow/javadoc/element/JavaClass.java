package thito.nodeflow.javadoc.element;

import thito.nodeflow.javadoc.*;
import thito.nodeflow.javadoc.element.reference.*;

public class JavaClass extends JavaMember {

    private String packageName;
    private String simpleName;
    private String moduleName;
    private ClassType type;
    private TypeReference superClass;
    private TypeReference[] interfaces;
    private JavaMember[] members; // does not includes inner classes!
    private String[] innerClasses;
    private TypeReference[] genericParameters;

    {
        memberType = "JavaClass";
    }

    public void setGenericParameters(TypeReference[] genericParameters) {
        this.genericParameters = genericParameters;
    }

    public TypeReference[] getGenericParameters() {
        return genericParameters;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getPackageName() {
        return packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ClassType getType() {
        return type;
    }

    public String[] getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(String[] innerClasses) {
        this.innerClasses = innerClasses;
    }

    public void setType(ClassType type) {
        this.type = type;
    }

    public TypeReference getSuperClass() {
        return superClass;
    }

    public void setSuperClass(TypeReference superClass) {
        this.superClass = superClass;
    }

    public TypeReference[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(TypeReference[] interfaces) {
        this.interfaces = interfaces;
    }

    public JavaMember[] getMembers() {
        return members;
    }

    public void setMembers(JavaMember[] members) {
        this.members = members;
    }
}
