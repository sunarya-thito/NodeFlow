package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.UnknownNodeHandler;
import thito.nodeflow.plugin.base.blueprint.state.*;

public class UnknownNodeProvider implements NodeProvider {
    @Override
    public String getId() {
        return "?";
    }

    @Override
    public I18n displayNameProperty() {
        return null;
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return new UnknownNodeHandler(node, handlerState);
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
    }

    @Override
    public Node createNode(BlueprintHandler blueprintHandler) {
        Node node = new Node();
        node.setHandler(createHandler(blueprintHandler, node, null));
        return node;
    }
}
