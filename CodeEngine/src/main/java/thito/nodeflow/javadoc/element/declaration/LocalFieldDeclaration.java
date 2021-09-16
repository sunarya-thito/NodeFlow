package thito.nodeflow.javadoc.element.declaration;

public class LocalFieldDeclaration extends TypeDeclaration {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String s = super.toString();
        return s.isEmpty() ? name : s + " " +name;
    }
}
