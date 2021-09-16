package thito.nodeflow.javadoc;

public class LocalFieldDeclaration extends TypeUseDeclaration {
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
