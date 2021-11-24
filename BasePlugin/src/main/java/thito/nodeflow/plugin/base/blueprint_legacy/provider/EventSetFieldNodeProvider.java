package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.language.I18n;
import thito.nodeflow.java.IField;
import thito.nodeflow.java.generated.LField;
import thito.nodeflow.java.known.KField;
import thito.nodeflow.plugin.base.blueprint_legacy.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.GetFieldNodeHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.ExecutionParameterHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.FieldInputValueParameterHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;

import java.lang.reflect.Field;

public class EventSetFieldNodeProvider implements NodeProvider {
    private Field field;

    public EventSetFieldNodeProvider(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getId() {
        return "baseplugin.setEventFieldNodeProvider." +
                field.getDeclaringClass().getName() +
                "#" +
                field.getName();
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
        Node node = nodeCompiler.getNode();
        IField field = new KField(getField());
        NodeParameter pair = node.getParameters().get(1).getPairs(true).stream().findAny().orElse(null);
        field.set(new LField(field.getDeclaringClass(), 0), pair == null ? null : context.getNodeCompiler(pair.getNode()).getValue(pair));
        node.getParameters().get(0).getPairs(false).stream().findAny().ifPresent(next -> context.compile(next.getNode()));
    }

    @Override
    public I18n displayNameProperty() {
        return I18n.$("plugin.blueprint.get-field-node").format(field.getName());
    }

    @Override
    public Node createNode(BlueprintHandler blueprintHandler) {
        Node node = new Node();
        GetFieldNodeHandler handler = new GetFieldNodeHandler(node, this, field);
        node.setHandler(handler);
        {
            // exec param
            NodeParameter exec = new NodeParameter();
            exec.setHandler(new ExecutionParameterHandler(exec));
            node.getParameters().add(exec);
        }
        {
            // input param
            NodeParameter input = new NodeParameter();
            input.setHandler(new FieldInputValueParameterHandler(handler.getGenericStorage(), getField(), input));
            node.getParameters().add(input);
        }
        return node;
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return new GetFieldNodeHandler(node, this, getField());
    }
}
