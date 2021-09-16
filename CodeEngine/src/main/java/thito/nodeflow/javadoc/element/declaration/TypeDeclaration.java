package thito.nodeflow.javadoc.element.declaration;

import thito.nodeflow.javadoc.element.*;

import java.util.*;
import java.util.stream.*;

public abstract class TypeDeclaration {
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
