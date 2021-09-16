package thito.nodeflow.javadoc.element.reference;

import thito.nodeflow.javadoc.element.*;
import thito.nodeflow.javadoc.element.declaration.*;

import java.util.*;
import java.util.stream.*;

public abstract class TypeReference {
    private JavaAnnotation[] annotations;
    protected String type;
    protected ArrayDimensionDeclaration[] arrayDimensions;

    public void setArrayDimensions(ArrayDimensionDeclaration[] arrayDimensions) {
        this.arrayDimensions = arrayDimensions != null && arrayDimensions.length == 0 ? null : arrayDimensions;
    }

    public void setAnnotations(JavaAnnotation[] annotations) {
        this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
    }

    public JavaAnnotation[] getAnnotations() {
        return annotations;
    }

    public ArrayDimensionDeclaration[] getArrayDimensions() {
        return arrayDimensions;
    }

    protected String toStringArray() {
        StringBuilder builder = new StringBuilder();
        if (annotations != null) {
            builder.append(Arrays.stream(annotations).map(Objects::toString).collect(Collectors.joining(" ")));
            builder.append(" ");
        }
        if (arrayDimensions != null) {
            builder.append(Arrays.stream(arrayDimensions).map(Objects::toString).collect(Collectors.joining()));
        }
        return builder.toString();
    }
}
