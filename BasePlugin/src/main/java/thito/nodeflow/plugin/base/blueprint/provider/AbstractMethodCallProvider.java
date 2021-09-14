package thito.nodeflow.plugin.base.blueprint.provider;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.handler.*;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.*;

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

    @Override
    public Node createNode() {
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
            }
        }
        return node;
    }
}
