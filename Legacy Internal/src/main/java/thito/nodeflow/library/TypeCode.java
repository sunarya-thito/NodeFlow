package thito.nodeflow.library;

import java.util.*;
import java.util.stream.*;

public class TypeCode {
    protected String name;
    protected TypeCode[] generics;

    public TypeCode(String name, TypeCode[] generics) {
        this.name = name;
        this.generics = generics;
    }

    public String getName() {
        return name;
    }

    public TypeCode[] getGenerics() {
        return generics;
    }

    public String toString() {
        if (generics == null) {
            return name;
        } else {
            return name + "<" + Arrays.stream(generics).map(TypeCode::toString).collect(Collectors.joining(", ")) + ">";
        }
    }
}
