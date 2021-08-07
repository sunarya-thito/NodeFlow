package thito.nodeflow.bytecode;

public class Annotated {
    private String name;
    private Object value;

    public Annotated(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
