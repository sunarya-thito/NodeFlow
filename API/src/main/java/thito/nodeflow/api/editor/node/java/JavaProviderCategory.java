package thito.nodeflow.api.editor.node.java;

import thito.nodeflow.api.editor.node.*;

public interface JavaProviderCategory extends NodeProviderCategory {
    Class<?> getType();

}
