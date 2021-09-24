package thito.nodeflow.plugin.base.blueprint.compiler;

import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.*;

import java.util.*;

public class JavaCompilerContext extends CompilerContext {
    private CompilerContext parent;
    private Map<NodeParameterState, ParameterValue> valueMap = new HashMap<>();

    public JavaCompilerContext(CompilerContext parent) {
        this.parent = parent;
    }

    @Override
    public void setParameterValue(NodeParameterState parameter, ParameterValue value) {
        if (parent != null) {
//
        }
        valueMap.put(parameter, value);
    }

    @Override
    public ParameterValue getParameterValue(NodeParameterState parameter) {
        return valueMap.get(parameter);
    }

    @Override
    public void prepareNode(NodeState nodeState) {

    }

    @Override
    public void compileNode(NodeState nodeState) {

    }

    @Override
    public CompilerContext createSubContext() {
        return new JavaCompilerContext(this);
    }
}
