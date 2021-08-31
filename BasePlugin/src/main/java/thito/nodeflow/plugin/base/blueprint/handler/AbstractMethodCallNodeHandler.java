package thito.nodeflow.plugin.base.blueprint.handler;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.handler.parameter.*;
import thito.nodeflow.plugin.base.blueprint.state.*;

import java.lang.reflect.*;

public abstract class AbstractMethodCallNodeHandler extends JavaNodeHandler {

    private boolean varargs;
    private BlueprintNodeState state;
    private GenericStorage genericStorage = new GenericStorage();

    public AbstractMethodCallNodeHandler(Node node, BlueprintNodeState state, boolean varargs) {
        super(node);
        this.state = state;
        this.varargs = varargs;
    }

    protected abstract Executable getExecutable();

    @Override
    public NodeSkin createSkin() {
        return new NodeSkin(getNode());
    }

    @Override
    public NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state) {
        if (state instanceof ParameterHandlerState) {
            int index = ((ParameterHandlerState) state).parameterIndex;
            Parameter param = getExecutable().getParameters()[index];
            if (varargs && param.isVarArgs()) {
                return new VarArgsParameterHandler(param, parameter);
            }
            return new InputParameterHandler(genericStorage, param, parameter);
        }
        if (state instanceof InstanceParameterHandlerState) {
            return new InstanceParameterHandler(genericStorage, getExecutable().getDeclaringClass(), parameter);
        }
        return null;
    }

    @Override
    public HandlerState saveState() {
        return state;
    }
}
