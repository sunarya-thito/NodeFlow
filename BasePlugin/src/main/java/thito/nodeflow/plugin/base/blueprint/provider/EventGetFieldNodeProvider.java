package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.java.IField;
import thito.nodeflow.java.generated.LField;
import thito.nodeflow.java.known.KField;
import thito.nodeflow.plugin.base.blueprint.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint.NodeProvider;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.EventGetFieldNodeHandler;
import thito.nodeflow.plugin.base.blueprint.handler.GetFieldNodeHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.ExecutionParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.InstanceParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.OutputParameterHandler;
import thito.nodeflow.plugin.base.blueprint.state.BlueprintNodeState;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EventGetFieldNodeProvider implements NodeProvider {
    private Field field;

    public EventGetFieldNodeProvider(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getId() {
        return "baseplugin.getEventFieldNodeProvider." +
                field.getDeclaringClass().getName() +
                "#" +
                field.getName();
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
        IField field = new KField(getField());
        Node node = nodeCompiler.getNode();
        NodeParameter result = node.getParameters().get(1);
        nodeCompiler.setValue(result, field.get(new LField(field.getDeclaringClass(), 0).get()));
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
            // result param
            NodeParameter result = new NodeParameter();
            result.setHandler(new OutputParameterHandler(handler.getGenericStorage(), getField().getGenericType(), result));
            node.getParameters().add(result);
        }
        return node;
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return new EventGetFieldNodeHandler(node, this, getField());
    }
}
