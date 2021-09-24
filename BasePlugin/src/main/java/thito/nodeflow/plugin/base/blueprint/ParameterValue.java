package thito.nodeflow.plugin.base.blueprint;

public interface ParameterValue {
    void setValue(CompilerContext context, Object value);
    Object getValue(CompilerContext context);
}
