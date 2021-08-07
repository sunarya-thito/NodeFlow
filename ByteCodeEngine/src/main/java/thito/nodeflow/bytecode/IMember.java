package thito.nodeflow.bytecode;

public interface IMember {
    String getName();
    int getModifiers();
    IClass getDeclaringClass();
}
