package thito.nodeflow.library;

public class Member {
    protected String name, description;
    protected int modifiers;
    protected AnnotationCode[] annotations;
    protected GenericTypeCode[] generics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public AnnotationCode[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(AnnotationCode[] annotations) {
        this.annotations = annotations;
    }

    public GenericTypeCode[] getGenerics() {
        return generics;
    }

    public void setGenerics(GenericTypeCode[] generics) {
        this.generics = generics;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifiers=" + modifiers +
                '}';
    }
}
