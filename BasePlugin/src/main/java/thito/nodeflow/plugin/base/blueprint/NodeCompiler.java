package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.state.*;

public interface NodeCompiler {
    NodeState getNodeState();
    void computeVariables(CompilerContext context);
    void computeExecutions(CompilerContext context);
}
