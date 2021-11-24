package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.*;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;

import java.lang.reflect.*;

public class GetFieldNodeProvider implements NodeProvider {
    private Field field;

    public GetFieldNodeProvider(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getId() {
        return "baseplugin.getFieldNodeProvider." +
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
            // result param
            NodeParameter result = new NodeParameter();
            result.setHandler(new FieldValueParameterHandler(handler.getGenericStorage(), getField(), result));
            node.getParameters().add(result);
        }
        return node;
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return new GetFieldNodeHandler(node, this, getField());
    }
}
