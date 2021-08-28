package thito.nodeflow.java;

public interface IMethod extends IMember {
    int getParameterCount();
    IClass getReturnType();
    IClass[] getParameterTypes();
    Reference invoke(Object instance, Object...args);
    void invokeVoid(Object instance, Object...args);
    IClass[] getThrows();
}
