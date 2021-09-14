package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.library.language.*;
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
        return "baseplugin.constructorNodeProvider." +
                getExecutable().getDeclaringClass().getName() +
                "(" +
                Arrays.stream(getExecutable().getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) +
                ");"+isVarargs();
    }

    @Override
    public I18n displayNameProperty() {
        Executable exec = getExecutable();
        return I18n.$("baseplugin.blueprint.constructor-node").format(
                exec.getDeclaringClass().getSimpleName(),
                exec.getDeclaringClass().getName(),
                exec.getDeclaringClass().getCanonicalName()
        );
    }

    @Override
    public NodeHandler createHandler(Node node, BlueprintNodeState handlerState) {
        return createHandler(node);
    }

    @Override
    protected AbstractMethodCallNodeHandler createHandler(Node node) {
        return new ConstructorNodeHandler(node, this, isVarargs(), (Constructor<?>) getExecutable());
    }
}
