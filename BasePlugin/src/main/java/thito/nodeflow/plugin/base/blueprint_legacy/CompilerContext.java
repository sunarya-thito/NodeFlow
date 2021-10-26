package thito.nodeflow.plugin.base.blueprint_legacy;

import thito.nodeflow.engine.node.state.*;

public abstract class CompilerContext {
    public abstract void prepareNode(NodeState nodeState);
    public abstract void compileNode(NodeState nodeState);
    public abstract void setParameterValue(NodeParameterState parameter, ParameterValue value);
    public abstract ParameterValue getParameterValue(NodeParameterState parameter);
    public abstract CompilerContext createSubContext();
}
