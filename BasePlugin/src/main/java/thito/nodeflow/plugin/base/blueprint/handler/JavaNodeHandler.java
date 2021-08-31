package thito.nodeflow.plugin.base.blueprint.handler;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.plugin.base.blueprint.*;

public abstract class JavaNodeHandler implements NodeHandler {
    private Node node;
    private GenericStorage genericStorage = new GenericStorage();

    public JavaNodeHandler(Node node) {
        this.node = node;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

}
