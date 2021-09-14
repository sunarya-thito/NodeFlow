package thito.nodeflow.plugin.base.blueprint.handler;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

public abstract class JavaNodeHandler implements NodeHandler {
    private Node node;
    private NodeProvider provider;
    private GenericStorage genericStorage = new GenericStorage();

    public JavaNodeHandler(Node node, NodeProvider provider) {
        this.node = node;
        this.provider = provider;
    }

    public NodeProvider getProvider() {
        return provider;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public abstract BlueprintNodeState saveState();
}
