package thito.nodeflow.java;

public abstract class ConstantReference extends Reference {
    private final Object constant;

    public ConstantReference(IClass type, Object constant) {
        super(type);
        this.constant = constant;
    }

    public ConstantReference(Class<?> clazz, Object constant) {
        super(clazz);
        this.constant = constant;
    }

    public Object getConstant() {
        return constant;
    }
}
