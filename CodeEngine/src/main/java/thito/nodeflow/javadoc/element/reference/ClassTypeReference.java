package thito.nodeflow.javadoc.element.reference;

import java.util.*;
import java.util.stream.*;

public class ClassTypeReference extends TypeReference {

    {
        type = "Class";
    }

    private String name;
    private TypeReference[] parameters;

    public ClassTypeReference() {
    }

    public ClassTypeReference(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(TypeReference[] parameters) {
        this.parameters = parameters;
    }

    public TypeReference[] getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        if (parameters != null && parameters.length > 0) {
            return name + "<" + Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(", ")) + ">" + toStringArray();
        }
        return name + toStringArray();
    }
}
