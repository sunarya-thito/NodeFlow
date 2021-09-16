package thito.nodeflow.javadoc.element.reference;

import java.util.*;
import java.util.stream.*;

public class WildcardTypeReference extends TypeReference {
    {
        type = "Wildcard";
    }

    private TypeReference[] upperBounds;
    private TypeReference[] lowerBounds;

    public void setLowerBounds(TypeReference[] lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    public void setUpperBounds(TypeReference[] upperBounds) {
        this.upperBounds = upperBounds;
    }

    public String toString() {
        if (upperBounds != null && upperBounds.length > 0) {
            return "? extends "+ Arrays.stream(upperBounds).map(Objects::toString).collect(Collectors.joining(" & "));
        }
        if (lowerBounds != null && lowerBounds.length > 0) {
            return "? super " + Arrays.stream(lowerBounds).map(Objects::toString).collect(Collectors.joining(" & "));
        }
        return "?";
    }
}
