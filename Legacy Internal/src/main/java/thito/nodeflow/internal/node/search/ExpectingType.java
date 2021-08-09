package thito.nodeflow.internal.node.search;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodejfx.parameter.type.*;

import java.lang.reflect.*;
import java.util.*;

public class ExpectingType {

    public static boolean isAssignableFrom(Class<?> target, Class<?> from) {
        if (target == boolean.class || target == Boolean.class) return from == boolean.class || from == Boolean.class;
        if (target.isPrimitive()) {
            return from != boolean.class && from.isPrimitive();
        }
        return CompoundType.isAssignableFrom(target, from);
    }

    private List<Class<?>> classes = new ArrayList<>();

    public ExpectingType(Class<?> cl) {
        this.classes.add(cl);
    }

    public ExpectingType(Collection<Class<?>> classes) {
        this.classes.addAll(classes);
        if (this.classes.isEmpty()) throw new IllegalArgumentException("Empty classes");
    }

    public boolean isAssignableFrom(Class<?> clazz) {
        for (Class<?> cl : classes) {
            if (isAssignableFrom(cl, clazz)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPrimitive() {
        for (Class<?> cl : classes) {
            if (cl.isPrimitive()) return true;
        }
        return false;
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public boolean isAssignableTo(Class<?> clazz) {
        for (Class<?> cl : classes) {
            if (isAssignableFrom(clazz, cl)) {
                return true;
            }
        }
        return false;
    }

    public int getParameterIndex(NodeProvider node, boolean input) {
        for (Class<?> cl : classes) {
            int index = ((AbstractNodeProvider) node).getParameterIndex(input, cl);
            if (index != -1) return index;
        }
        return -1;
    }


    public Method getMethod(String name, Class<?>... classes) {
        for (Class<?> cl : this.classes) {
            try {
                return cl.getMethod(name, classes);
            } catch (Throwable t) {
            }
        }
        return null;
    }
}
