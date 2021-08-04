package thito.nodeflow.library;

public class GenericTypeCode extends TypeCode {
    protected GenericDirection direction = GenericDirection.EXTENDS;

    public GenericTypeCode(String name, TypeCode[] generics) {
        super(name, generics);
    }

    public GenericDirection getDirection() {
        return direction;
    }
}
