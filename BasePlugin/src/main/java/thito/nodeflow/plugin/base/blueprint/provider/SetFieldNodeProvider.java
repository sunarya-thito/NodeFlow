package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.plugin.base.blueprint.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint.NodeProvider;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.GetFieldNodeHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.ExecutionParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.FieldInputValueParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.InstanceParameterHandler;
import thito.nodeflow.plugin.base.blueprint.state.BlueprintNodeState;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SetFieldNodeProvider implements NodeProvider {
    private Field field;

    public SetFieldNodeProvider(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getId() {
        return "baseplugin.setFieldNodeProvider." +
                field.getDeclaringClass().getName() +
                "#" +
                field.getName();
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {

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
            // instance param
            if (!Modifier.isStatic(field.getModifiers())) {
                NodeParameter instance = new NodeParameter();
                instance.setHandler(new InstanceParameterHandler(handler.getGenericStorage(), getField().getDeclaringClass(), instance));
                node.getParameters().add(instance);
            }
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
