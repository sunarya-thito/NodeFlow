package thito.nodeflow.bytecode.util;

import thito.nodeflow.bytecode.*;

public class Condition {
    public static Condition Is(Object obj) {
        return new Condition(obj);
    }

    private Object object;

    public Condition(Object object) {
        this.object = object;
    }

    public Reference NotNull() {
        return null;
    }

    public Reference Null() {
        return null;
    }

    public Reference greaterThan(Object compare) {
        return null;
    }

    public Reference SameInstance(Object compare) {
        return null;
    }

    public Reference LessThan(Object compare) {
        return null;
    }

    public Reference EqualTo(Object compare) {
        return null;
    }

    public Reference GreaterThanOrEqualTo(Object compare) {
        return null;
    }

    public Reference LessThanOrEqualTo(Object compare) {
        return null;
    }
}
