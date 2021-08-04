package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;

public interface NodeProvider {
    String getID();
    String getDescription();
    NodeProviderCategory getCategory();
    String getName();
    Node createComponent(NodeModule module);
    Node fromState(NodeModule module, ComponentState state);
    NodeCompileSession createCompileSession(Node node);
}
