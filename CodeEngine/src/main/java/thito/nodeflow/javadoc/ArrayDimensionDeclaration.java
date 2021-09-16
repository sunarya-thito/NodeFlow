package thito.nodeflow.javadoc;

public class ArrayDimensionDeclaration extends TypeUseDeclaration {
    @Override
    public String toString() {
        String s = super.toString();
        return s.isEmpty() ? "[]" : s+"[]";
    }
}
