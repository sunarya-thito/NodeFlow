package thito.nodeflow.internal.node.headless;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;

public class HeadlessGroup implements NodeGroup {
    private GroupState state;
    private NodeModule module;

    public HeadlessGroup(NodeModule module, GroupState state) {
        this.state = state;
        this.module = module;
    }

    @Override
    public GroupState getState() {
        return state;
    }

    @Override
    public void remove() {
        ((AbstractNodeModule) module).groups().remove(this);
    }
}
