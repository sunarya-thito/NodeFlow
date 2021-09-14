package thito.nodeflow.plugin.base.blueprint.handler;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

import java.lang.reflect.*;

public abstract class AbstractMethodCallNodeHandler extends JavaNodeHandler {

    private boolean varargs;

    public AbstractMethodCallNodeHandler(Node node, NodeProvider nodeProvider, boolean varargs) {
        super(node, nodeProvider);
        this.varargs = varargs;
    }

    protected abstract Executable getExecutable();

    @Override
    public NodeSkin createSkin() {
        return new NodeSkin(getNode());
    }

    @Override
    public NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state) {
        if (state instanceof ExecutionParameterHandlerState) {
            return new ExecutionParameterHandler(parameter);
        }
        if (state instanceof ParameterHandlerState) {
            int index = ((ParameterHandlerState) state).parameterIndex;
            Parameter param = getExecutable().getParameters()[index];
            if (varargs && param.isVarArgs()) {
                VarArgsParameterHandler handler = new VarArgsParameterHandler(getGenericStorage(), param, parameter);
                handler.setValue(((ParameterHandlerState) state).value);
                return handler;
            }
            InputParameterHandler handler = new InputParameterHandler(getGenericStorage(), param, parameter);
            handler.setValue(((ParameterHandlerState) state).value);
            return handler;
        }
        if (state instanceof InstanceParameterHandlerState) {
            InstanceParameterHandler handler = new InstanceParameterHandler(getGenericStorage(), getExecutable().getDeclaringClass(), parameter);
            handler.setValue(((InstanceParameterHandlerState) state).value);
            return handler;
        }
        if (state instanceof OutputParameterHandlerState) {
            return new OutputParameterHandler(getGenericStorage(), ((Method) getExecutable()).getGenericReturnType(), parameter);
        }
        return null;
    }
}
