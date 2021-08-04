package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;

import java.util.*;

public interface Node extends ModuleMember {
    NodeModule getModule();
    ComponentState getState();
    List<NodeParameter> getParameters();
    default <T extends NodeParameter> T getParameter(int index) {
        return (T) getParameters().get(index);
    }
}
