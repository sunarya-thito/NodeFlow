package thito.nodeflow.plugin.base.blueprint_legacy;

public interface ParameterValue {
    void setValue(CompilerContext context, Object value);
    Object getValue(CompilerContext context);
}
