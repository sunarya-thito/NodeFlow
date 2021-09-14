package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.plugin.base.blueprint.handler.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class MethodInvocationNodeProvider extends AbstractMethodCallProvider {

    public MethodInvocationNodeProvider(Method method, boolean varargs) {
        super(method, varargs);
    }

    @Override
    public Method getExecutable() {
        return (Method) super.getExecutable();
    }

    @Override
    public String getId() {
        return "baseplugin.methodInvocationNodeProvider." +
                getExecutable().getDeclaringClass().getName() +
                "#" +
                getExecutable().getName() +
                "(" +
                Arrays.stream(getExecutable().getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) +
                ");"+isVarargs();
    }

    @Override
    public I18n displayNameProperty() {
        return I18n.$("baseplugin.blueprint.method-node").format(getExecutable().getName());
    }

    @Override
    public NodeHandler createHandler(Node node, BlueprintNodeState handlerState) {
        return createHandler(node);
    }

    @Override
    protected AbstractMethodCallNodeHandler createHandler(Node node) {
        return new MethodInvocationNodeHandler(node, this, isVarargs(), getExecutable());
    }
}
