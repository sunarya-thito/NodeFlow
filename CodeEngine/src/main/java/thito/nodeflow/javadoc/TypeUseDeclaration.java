package thito.nodeflow.javadoc;

import java.util.*;
import java.util.stream.*;

public class TypeUseDeclaration {
    private JavaAnnotation[] annotations;

    public void setAnnotations(JavaAnnotation[] annotations) {
        this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
    }

    public JavaAnnotation[] getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        return annotations == null ? "" : Arrays.stream(annotations).map(Objects::toString).collect(Collectors.joining(" "));
    }
}
