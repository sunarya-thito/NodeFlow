package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.EventStartNodeHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;

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
