package thito.nodeflow.internal.node.provider;

import java.lang.reflect.*;
import java.util.*;

public class GenericArrayTypeImpl implements GenericArrayType {
    private Type component;

    public GenericArrayTypeImpl(Type component) {
        this.component = component;
    }

    @Override
    public Type getGenericComponentType() {
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericArrayType)) return false;
        GenericArrayTypeImpl that = (GenericArrayTypeImpl) o;
        return component.equals(that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }
}
