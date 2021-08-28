package thito.nodeflow.javadoc;

public class JavaClass extends JavaMember {

    private String moduleName;
    private ClassType type;
    private String superClass;
    private String[] interfaces;
    private JavaMember[] members; // does not includes inner classes!
    private String[] innerClasses;

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

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public JavaMember[] getMembers() {
        return members;
    }

    public void setMembers(JavaMember[] members) {
        this.members = members;
    }
}
