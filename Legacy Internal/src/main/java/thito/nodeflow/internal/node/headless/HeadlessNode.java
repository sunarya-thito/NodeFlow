package thito.nodeflow.internal.node.headless;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;

import java.util.*;

public class HeadlessNode implements Node {
    private NodeModule module;
    private ComponentState state;
    private List<NodeParameter> parameters = new ArrayList<>();

    public HeadlessNode(NodeModule module, ComponentState state) {
        this.module = module;
        this.state = state;
    }

    @Override
    public void remove() {
        ((AbstractNodeModule) module).nodes().remove(this);
    }

    @Override
    public NodeModule getModule() {
        return module;
    }

    @Override
    public ComponentState getState() {
        return state;
    }

    @Override
    public List<NodeParameter> getParameters() {
        return parameters;
    }
}
