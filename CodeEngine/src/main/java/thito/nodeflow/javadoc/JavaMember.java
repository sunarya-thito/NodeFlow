package thito.nodeflow.javadoc;

import java.util.*;

public class JavaMember {
    protected String memberType;
    private int modifiers;
    private String name;

    private String comment;
    private JavaAnnotation[] annotations;
    private Map<String, List<String>> tagMap = new HashMap<>();

    public JavaAnnotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(JavaAnnotation[] annotations) {
        this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public Map<String, List<String>> getTagMap() {
        return tagMap;
    }
}
