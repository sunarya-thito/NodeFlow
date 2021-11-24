package thito.nodeflow.plugin.base.blueprint_legacy;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;

public interface NodeProvider {
    String getId();
    I18n displayNameProperty();
    Node createNode(BlueprintHandler blueprintHandler);
    NodeHandler createHandler(BlueprintHandler handler, Node node, BlueprintNodeState handlerState);
    void compile(CompilerContext context, NodeCompiler nodeCompiler);
}
