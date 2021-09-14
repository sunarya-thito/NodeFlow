package thito.nodeflow.plugin.base.blueprint;

public interface NodeCompiler {
    void computeVariables(CompilerContext context);
    void computeExecutions(CompilerContext context);
}
