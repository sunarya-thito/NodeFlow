package thito.nodeflow.javadoc;

import java.util.*;

public class JavaMember {
    private int modifiers;
    private String name;

    private String comment;
    private Map<String, String> tagMap = new HashMap<>();

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

    public Map<String, String> getTagMap() {
        return tagMap;
    }
}
