package thito.nodeflow.javadoc;

import java.util.*;
import java.util.stream.*;

public class VariableTypeReference extends TypeReference {

    {
        type = "Variable";
    }

    private String owner;
    private String name;
    private TypeReference[] upperBounds;
    private TypeReference[] lowerBounds;

    public void setUpperBounds(TypeReference[] upperBounds) {
        this.upperBounds = upperBounds;
    }

    public void setLowerBounds(TypeReference[] lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    public TypeReference[] getUpperBounds() {
        return upperBounds;
    }

    public TypeReference[] getLowerBounds() {
        return lowerBounds;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        if (upperBounds != null && upperBounds.length > 0) {
            return name + " extends " + Arrays.stream(upperBounds).map(Object::toString).collect(Collectors.joining(" & "));
        }
        if (lowerBounds != null && lowerBounds.length > 0) {
            return name + " super " + Arrays.stream(lowerBounds).map(Object::toString).collect(Collectors.joining(" & "));
        }
        return name + toStringArray();
    }
}
