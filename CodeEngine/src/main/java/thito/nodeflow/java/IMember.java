package thito.nodeflow.java;

public interface IMember {
    String getName();
    int getModifiers();
    IClass getDeclaringClass();
    Annotated[] getAnnotations();
    default Annotated getAnnotation(IClass type) {
        for (Annotated annotated : getAnnotations()) {
            if (annotated.equals(type)) {
                return annotated;
            }
        }
        return null;
    }
}
