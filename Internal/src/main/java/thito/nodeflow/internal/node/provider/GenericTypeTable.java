package thito.nodeflow.internal.node.provider;

import java.lang.reflect.*;
import java.util.*;

public class GenericTypeTable {

    public static GenericTypeTable create(GenericDeclaration declaration) {
        GenericTypeTable table = new GenericTypeTable();
        collect(table, declaration);
        return table;
    }

    public static GenericTypeTable collect(GenericTypeTable table, GenericDeclaration declaration) {
        if (declaration == null) return table;
        if (declaration instanceof Member) {
            collect(table, (GenericDeclaration) ((Member) declaration).getDeclaringClass());
        }
        for (Type type : declaration.getTypeParameters()) {
            collect(table, type);
        }
        return table;
    }

    private static void collect(GenericTypeTable table, Type type) {
        if (type instanceof TypeVariable) {
            table.variables.add((TypeVariable) type);
        } else if (type instanceof GenericArrayType) {
            collect(table, ((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof WildcardType) {
            throw new IllegalStateException("illegal declaration");
        } else if (type instanceof ParameterizedType) {
            for (Type t : ((ParameterizedType) type).getActualTypeArguments()) {
                collect(table, t);
            }
        }
    }

    private Set<TypeVariable> variables = new HashSet<>();

    public Set<TypeVariable> getVariables() {
        return variables;
    }
}
