package thito.nodeflow.javadoc;

public class JavaField extends JavaMember {

    {
        memberType = "JavaField";
    }

    private TypeReference type;

    public TypeReference getType() {
        return type;
    }

    public void setType(TypeReference type) {
        this.type = type;
    }
}
