package thito.nodeflow.library;

public class AnnotationCode {
    protected Group group;
    protected String type;

    public AnnotationCode(String type, Group group) {
        this.type = type;
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public String getType() {
        return type;
    }
}
