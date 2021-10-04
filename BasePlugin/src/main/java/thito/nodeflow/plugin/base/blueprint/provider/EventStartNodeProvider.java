package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.plugin.base.blueprint.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint.NodeProvider;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.EventStartNodeHandler;
import thito.nodeflow.plugin.base.blueprint.state.BlueprintNodeState;
import thito.nodeflow.plugin.base.blueprint.state.EventStartNodeState;

public class EventStartNodeProvider implements NodeProvider {
    @Override
    public String getId() {
        return "eventStart";
    }

    @Override
    public I18n displayNameProperty() {
        return I18n.$("plugin.blueprint.event-start-node");
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {

    }

    @Override
    public Node createNode(BlueprintHandler blueprintHandler) {
        Node node = new Node();
        EventStartNodeHandler handler = createHandler(blueprintHandler, node, null);
        node.setHandler(handler);
        return node;
    }

    @Override
    public EventStartNodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        EventStartNodeHandler handler = new EventStartNodeHandler(node, this);

        return handler;
    }
}
