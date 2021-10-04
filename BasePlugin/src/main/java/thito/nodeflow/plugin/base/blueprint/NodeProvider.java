package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.state.BlueprintNodeState;

public interface NodeProvider {
    String getId();
    I18n displayNameProperty();
    Node createNode(BlueprintHandler blueprintHandler);
    NodeHandler createHandler(BlueprintHandler handler, Node node, BlueprintNodeState handlerState);
    void compile(CompilerContext context, NodeCompiler nodeCompiler);
}
