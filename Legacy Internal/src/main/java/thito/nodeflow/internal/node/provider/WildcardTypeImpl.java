package thito.nodeflow.internal.node.provider;

import java.lang.reflect.*;

public class WildcardTypeImpl implements WildcardType {
    private Type[] upper, lower;

    public WildcardTypeImpl(Type[] upper, Type[] lower) {
        this.upper = upper;
        this.lower = lower;
    }

    @Override
    public Type[] getUpperBounds() {
        return upper;
    }

    @Override
    public Type[] getLowerBounds() {
        return lower;
    }
}
