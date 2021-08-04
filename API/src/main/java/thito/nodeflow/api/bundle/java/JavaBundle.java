package thito.nodeflow.api.bundle.java;

import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.editor.node.*;

import java.util.Set;

public interface JavaBundle extends Bundle {
    Set<String> getAvailableClasses();

    Class<?> findClass(String name);

    BundleClassLoader getClassLoader();

    boolean isShaded();
}
