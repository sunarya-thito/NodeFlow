package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.state.*;

public abstract class CompilerContext {
    public abstract void setParameterValue(NodeParameterState parameter, ParameterValue value);
    public abstract ParameterValue getParameterValue(NodeParameterState parameter);
}
