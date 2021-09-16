package thito.nodeflow.javadoc.element.declaration;

public class ArrayDimensionDeclaration extends TypeDeclaration {
    @Override
    public String toString() {
        String s = super.toString();
        return s.isEmpty() ? "[]" : s+"[]";
    }
}
