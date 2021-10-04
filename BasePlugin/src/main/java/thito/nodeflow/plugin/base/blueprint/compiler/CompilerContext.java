package thito.nodeflow.plugin.base.blueprint.compiler;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.plugin.base.blueprint.handler.BlueprintNodeHandler;

import java.util.ArrayList;
import java.util.List;

public class CompilerContext {
    private CompilerHandler handler;
    private List<NodeCompiler> nodes = new ArrayList<>();

    public CompilerContext(CompilerHandler handler) {
        this.handler = handler;
    }

    public CompilerHandler getHandler() {
        return handler;
    }

    public void compile(Node node) {
        NodeCompiler compiler = getNodeCompiler(node);
        compiler.getProvider().compile(this, compiler);
        compiler.setCompiled(true);
    }

    public NodeCompiler getNodeCompiler(Node node) {
        for (NodeCompiler c : nodes) {
            if (c.getNode() == node) {
                return c;
            }
        }
        NodeCompiler c = new NodeCompiler(this, node, ((BlueprintNodeHandler) node.getHandler()).getProvider());
        nodes.add(c);
        return c;
    }
}
