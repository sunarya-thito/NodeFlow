package thito.nodeflow.internal.node.provider;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ParameterizedTypeImpl implements ParameterizedType {
    private Type rawType, ownerType;
    private Type[] actualTypeArguments;

    public ParameterizedTypeImpl(Class<?> cl) {
        rawType = cl;
        actualTypeArguments = cl.getTypeParameters();
    }

    public ParameterizedTypeImpl(Type rawType, Type ownerType, Type[] actualTypeArguments) {
        this.rawType = rawType;
        this.ownerType = ownerType;
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    public String toString() {
        return rawType.getTypeName()+"<"+ Arrays.stream(actualTypeArguments).map(Type::getTypeName).collect(Collectors.joining(","))+">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterizedType)) return false;
        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        return rawType.equals(that.rawType) && Objects.equals(ownerType, that.ownerType) && Arrays.equals(actualTypeArguments, that.actualTypeArguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rawType, ownerType);
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        return result;
    }
}
