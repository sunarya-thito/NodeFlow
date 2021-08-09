package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.state.*;

import java.util.*;

public interface NodeParameter {
    UUID getID();
    String getName();
    Node getNode();
    Object getValue();
    void setValue(Object value);
    ParameterEditor getEditor();
    void setInputMode(LinkMode mode);
    void setOutputMode(LinkMode mode);
    LinkMode getInputMode();
    LinkMode getOutputMode();
    Object impl_getType();
    Object impl_getPeer();
    ComponentParameterState getState();
    Set<NodeParameter> collectOutputLinks();
    Set<NodeParameter> collectInputLinks();
    NodeParameter getOutputLink();
    NodeParameter getInputLink();
    boolean hasOutputLink();
    boolean hasInputLink();
}
