package thito.nodeflow.plugin.base.blueprint_legacy.compiler;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;

import java.util.HashMap;
import java.util.Map;

public class NodeCompiler {
    private CompilerContext context;
    private Node node;
    private NodeProvider provider;
    private Map<NodeParameter, Object> valueMap = new HashMap<>();
    private boolean compiled;

    public NodeCompiler(CompilerContext context, Node node, NodeProvider provider) {
        this.context = context;
        this.node = node;
        this.provider = provider;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    public NodeProvider getProvider() {
        return provider;
    }

    public Node getNode() {
        return node;
    }

    public void setValue(NodeParameter parameter, Object value) {
        if (parameter.getNode() != node) throw new IllegalArgumentException("not the same node");
        valueMap.put(parameter, value);
    }

    public Object getValue(NodeParameter parameter) {
        if (!isCompiled()) {
            context.compile(getNode());
        }
        return valueMap.get(parameter);
    }

}
