package thito.nodeflow.plugin.base.blueprint.shaded.standard;

import thito.nodeflow.plugin.base.blueprint.shaded.*;

public class JavaMathProvider {

    @Export
    public Number add(Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() + b.doubleValue();
        }
        if (a instanceof Float || b instanceof Float) {
            return a.floatValue() + b.floatValue();
        }
        if (a instanceof Long || b instanceof Long) {
            return a.longValue() + b.longValue();
        }
        return a.intValue() + b.intValue();
    }

    @Export
    public Number subtract(Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() - b.doubleValue();
        }
        if (a instanceof Float || b instanceof Float) {
            return a.floatValue() - b.floatValue();
        }
        if (a instanceof Long || b instanceof Long) {
            return a.longValue() - b.longValue();
        }
        return a.intValue() - b.intValue();
    }
}
