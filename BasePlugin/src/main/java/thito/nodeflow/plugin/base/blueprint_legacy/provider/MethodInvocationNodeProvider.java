package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.java.IMethod;
import thito.nodeflow.java.known.KMethod;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint_legacy.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;

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
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
        IMethod method = new KMethod(getExecutable());
        Node node = nodeCompiler.getNode();
    }

    @Override
    public I18n displayNameProperty() {
        return I18n.$("plugin.blueprint.method-node").format(getExecutable().getName());
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return createHandler(node);
    }

    @Override
    protected AbstractMethodCallNodeHandler createHandler(Node node) {
        return new MethodInvocationNodeHandler(node, this, isVarargs(), getExecutable());
    }
}
