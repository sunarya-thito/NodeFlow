package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.InsertFunction;
import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.java.IConstructor;
import thito.nodeflow.java.IMethod;
import thito.nodeflow.java.Java;
import thito.nodeflow.java.Reference;
import thito.nodeflow.java.generated.LField;
import thito.nodeflow.java.known.KConstructor;
import thito.nodeflow.java.known.KMethod;
import thito.nodeflow.java.util.Array;
import thito.nodeflow.plugin.base.blueprint.BlueprintHandler;
import thito.nodeflow.plugin.base.blueprint.NodeProvider;
import thito.nodeflow.plugin.base.blueprint.compiler.CompilerContext;
import thito.nodeflow.plugin.base.blueprint.compiler.JavaCompilerHandler;
import thito.nodeflow.plugin.base.blueprint.compiler.NodeCompiler;
import thito.nodeflow.plugin.base.blueprint.handler.AbstractMethodCallNodeHandler;
import thito.nodeflow.plugin.base.blueprint.handler.EventMethodInvocationNodeHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.ExecutionParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.InputParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.OutputParameterHandler;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.VarArgsParameterHandler;
import thito.nodeflow.plugin.base.blueprint.state.BlueprintNodeState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class EventMethodInvocationProvider implements NodeProvider {
    private Executable executable;
    private boolean varargs;

    public EventMethodInvocationProvider(Executable executable, boolean varargs) {
        this.executable = executable;
        this.varargs = varargs;
    }

    public boolean isVarargs() {
        return varargs;
    }

    public Executable getExecutable() {
        return executable;
    }

    protected AbstractMethodCallNodeHandler createHandler(Node node) {
        return new EventMethodInvocationNodeHandler(node, this, isVarargs(), (Method) getExecutable());
    }

    @Override
    public void compile(CompilerContext context, NodeCompiler nodeCompiler) {
        IMethod method = new KMethod((Method) getExecutable());
        Node node = nodeCompiler.getNode();
        NodeParameter newInstance = node.getParameters().get(node.getParameters().size() - 1);
        Collection<? extends NodeParameter> newInstancePairs = newInstance.getPairs(false);
        Object[] parameters = new Object[getExecutable().getParameterCount()];
        for (int i = 0; i < parameters.length; i++) {
            NodeParameter parameter = node.getParameters().get(i + 1);
            NodeParameter pair = parameter.getPairs(true).stream().findAny().orElse(null);
            if (isVarargs() && i == node.getParameters().size() - 1) {
                Object[] values = new Object[AbstractMethodCallProvider.countVarArgs(node)];
                Reference array = Array.newArray(Java.Class(getExecutable().getParameterTypes()[i].getComponentType()), values);
                parameters[i] = array;
            } else {
                parameters[i] = pair == null ? null : context.getNodeCompiler(pair.getNode()).getValue(pair);
            }
        }
        if (newInstancePairs.size() == 1) {
            nodeCompiler.setValue(newInstance, method.invoke(new LField(method.getDeclaringClass(), 0), parameters));
        } else if (!newInstancePairs.isEmpty()) {
            LField local = ((JavaCompilerHandler) context.getHandler()).getMethodBodyAccessor().createLocal(method.getDeclaringClass());
            local.set(method.invoke(new LField(method.getDeclaringClass(), 0), parameters));
            nodeCompiler.setValue(newInstance, local.get());
        } else {
            method.invokeVoid(new LField(method.getDeclaringClass(), 0), parameters);
        }
        node.getParameters().get(0).getPairs(false).stream().findAny().ifPresent(next -> context.compile(next.getNode()));
    }

    @Override
    public Node createNode(BlueprintHandler blueprintHandler) {
        Node node = new Node();
        AbstractMethodCallNodeHandler handler = createHandler(node);
        node.setHandler(handler);
        {
            // exec param
            NodeParameter exec = new NodeParameter();
            exec.setHandler(new ExecutionParameterHandler(exec));
            node.getParameters().add(exec);
        }
        {
            // method parameters
            Parameter[] parameters = executable.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                NodeParameter parameter = new NodeParameter();
                Parameter param = parameters[i];
                if (i == parameters.length - 1 && varargs) {
                    parameter.setHandler(new VarArgsParameterHandler(handler.getGenericStorage(), param, parameter));
                    parameter.nextInsertProperty().set(new InsertFunction(canvas -> {
                        NodeParameter insertion = new NodeParameter();
                        insertion.setHandler(new VarArgsParameterHandler(handler.getGenericStorage(), param, parameter));
                        return insertion;
                    }));
                } else {
                    parameter.setHandler(new InputParameterHandler(handler.getGenericStorage(), param, parameter));
                }
                node.getParameters().add(parameter);
            }
        }
        {
            // return type
            if (executable instanceof Method && ((Method) executable).getReturnType() != void.class) {
                NodeParameter returnValue = new NodeParameter();
                returnValue.setHandler(new OutputParameterHandler(handler.getGenericStorage(), ((Method) executable).getGenericReturnType(), returnValue));
                node.getParameters().add(returnValue);
            }
        }
        return node;
    }

    @Override
    public String getId() {
        return "baseplugin.eventMethodInvocationNodeProvider." +
                getExecutable().getDeclaringClass().getName() +
                "#" +
                getExecutable().getName() +
                "(" +
                Arrays.stream(getExecutable().getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) +
                ");"+isVarargs();
    }

    @Override
    public I18n displayNameProperty() {
        return I18n.$("plugin.blueprint.method-node").format(getExecutable().getName());
    }

    @Override
    public NodeHandler createHandler(BlueprintHandler blueprintHandler, Node node, BlueprintNodeState handlerState) {
        return createHandler(node);
    }
}
