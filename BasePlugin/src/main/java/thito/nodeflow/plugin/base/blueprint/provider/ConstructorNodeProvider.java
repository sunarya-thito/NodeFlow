package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.LField;
import thito.nodeflow.java.generated.body.MethodBodyAccessor;
import thito.nodeflow.java.known.KConstructor;
import thito.nodeflow.java.util.Array;
import thito.nodeflow.plugin.base.blueprint.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint.compiler.CompileHelper;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.JavaCompilerHandler;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ConstructorNodeProvider extends AbstractMethodCallProvider {

    public ConstructorNodeProvider(Constructor<?> constructor, boolean varargs) {
        super(constructor, varargs);
    }

    @Override
    public String getId() {
        return "blueprint.constructorNodeProvider." +
                getExecutable().getDeclaringClass().getName() +
                "(" +
                Arrays.stream(getExecutable().getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) +
                ");"+isVarargs();
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
        IConstructor constructor = new KConstructor((Constructor<?>) getExecutable());
        Node node = nodeCompiler.getNode();
        NodeParameter newInstance = node.getParameters().get(node.getParameters().size() - 1);
        Collection<? extends NodeParameter> newInstancePairs = newInstance.getPairs(false);
        Object[] parameters = new Object[getExecutable().getParameterCount()];
        for (int i = 0; i < parameters.length; i++) {
            NodeParameter parameter = node.getParameters().get(i + 1);
            NodeParameter pair = parameter.getPairs(true).stream().findAny().orElse(null);
            if (isVarargs() && i == node.getParameters().size() - 1) {
                Object[] values = new Object[countVarArgs(node)];
                Reference array = Array.newArray(Java.Class(getExecutable().getParameterTypes()[i].getComponentType()), values);
                parameters[i] = array;
            } else {
                parameters[i] = pair == null ? null : context.getNodeCompiler(pair.getNode()).getValue(pair);
            }
        }
        if (newInstancePairs.size() == 1) {
            nodeCompiler.setValue(newInstance, constructor.newInstance(parameters));
        } else if (!newInstancePairs.isEmpty()) {
            LField local = ((JavaCompilerHandler) context.getHandler()).getMethodBodyAccessor().createLocal(constructor.getDeclaringClass());
            local.set(constructor.newInstance(parameters));
            nodeCompiler.setValue(newInstance, local.get());
        } else {
            constructor.newInstanceVoid(parameters);
        }
        node.getParameters().get(0).getPairs(false).stream().findAny().ifPresent(next -> context.compile(next.getNode()));
    }

    @Override
    public I18n displayNameProperty() {
        Executable exec = getExecutable();
        return I18n.$("plugin.blueprint.constructor-node").format(
                exec.getDeclaringClass().getSimpleName(),
                exec.getDeclaringClass().getName(),
                exec.getDeclaringClass().getCanonicalName()
        );
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return createHandler(node);
    }

    @Override
    protected AbstractMethodCallNodeHandler createHandler(Node node) {
        return new ConstructorNodeHandler(node, this, isVarargs(), (Constructor<?>) getExecutable());
    }
}
