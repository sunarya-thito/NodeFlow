package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;

public interface NodeParameterFactory {
    String getName();
    NodeParameter createParameter(Node node, ComponentParameterState state);
    Class<?> getType();
    LinkMode getInputMode();
    LinkMode getOutputMode();
}
