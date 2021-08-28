package thito.nodeflow.java;

public class EnumValue {
    private IClass type;
    private String name;

    public EnumValue(IClass type, String name) {
        this.type = type;
        this.name = name;
    }

    public IClass getType() {
        return type;
    }

    public String name() {
        return name;
    }
}
