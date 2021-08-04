package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.state.*;

public interface NodeGroup extends ModuleMember {
    GroupState getState();
}
