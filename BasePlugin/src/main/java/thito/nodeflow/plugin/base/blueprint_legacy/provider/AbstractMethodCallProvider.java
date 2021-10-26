package thito.nodeflow.plugin.base.blueprint_legacy.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.*;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.*;

import java.lang.reflect.*;

public abstract class AbstractMethodCallProvider implements NodeProvider {
    private Executable executable;
    private boolean varargs;

    public AbstractMethodCallProvider(Executable executable, boolean varargs) {
        this.executable = executable;
        this.varargs = varargs;
    }

    public boolean isVarargs() {
        return varargs;
    }

    public Executable getExecutable() {
        return executable;
    }

    protected abstract AbstractMethodCallNodeHandler createHandler(Node node);

    public static int countVarArgs(Node node) {
        int count = 0;
        for (NodeParameter parameter : node.getParameters()) {
            if (parameter.getHandler() instanceof VarArgsParameterHandler) {
                count++;
            }
        }
        return count;
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
            // instance param
            if (executable instanceof Method && !Modifier.isStatic(executable.getModifiers())) {
                NodeParameter instance = new NodeParameter();
                instance.setHandler(new InstanceParameterHandler(handler.getGenericStorage(), executable.getDeclaringClass(), instance));
                node.getParameters().add(instance);
            }
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
            } else if (executable instanceof Constructor) {
                NodeParameter newInstance = new NodeParameter();
                newInstance.setHandler(new OutputParameterHandler(handler.getGenericStorage(), executable.getDeclaringClass(), newInstance));
                node.getParameters().add(newInstance);
            }
        }
        return node;
    }
}
