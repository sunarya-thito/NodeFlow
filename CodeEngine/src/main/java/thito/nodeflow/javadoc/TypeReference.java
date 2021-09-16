package thito.nodeflow.javadoc;

import java.io.*;
import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

public abstract class TypeReference {
    private JavaAnnotation[] annotations;
    protected String type;
    protected TypeUseDeclaration[] arrayDimensions;

    public void setArrayDimensions(TypeUseDeclaration[] arrayDimensions) {
        this.arrayDimensions = arrayDimensions != null && annotations.length == 0 ? null : arrayDimensions;
    }

    public void setAnnotations(JavaAnnotation[] annotations) {
        this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
    }

    public JavaAnnotation[] getAnnotations() {
        return annotations;
    }

    public TypeUseDeclaration[] getArrayDimensions() {
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
