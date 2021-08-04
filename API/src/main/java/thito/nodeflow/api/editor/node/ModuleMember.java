package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.state.*;

public interface ModuleMember {
    State getState();
    void remove();
}
