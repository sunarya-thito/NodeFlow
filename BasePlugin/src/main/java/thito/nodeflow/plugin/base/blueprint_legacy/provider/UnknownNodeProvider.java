package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.UnknownNodeHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;

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
