package thito.nodeflow.plugin.base.blueprint.compiler;

import thito.nodeflow.java.generated.body.MethodBodyAccessor;

public class JavaCompilerHandler implements CompilerHandler {
    private MethodBodyAccessor methodBodyAccessor;

    public MethodBodyAccessor getMethodBodyAccessor() {
        return methodBodyAccessor;
    }
}
