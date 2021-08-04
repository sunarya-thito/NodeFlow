package thito.nodeflow.api.bundle.java;

import thito.nodeflow.api.*;

import java.util.*;

public interface RuntimeJavaBundle extends JavaBundle {
    void addClass(Class<?> clazz);
    void removeClass(Class<?> clazz);
    void clearClasses();
    Set<Class<?>> getClasses();
    void setShaded(boolean shaded);
    static RuntimeJavaBundle create() {
        return NodeFlow.getApplication().getBundleManager().createDynamicBundle();
    }
}
