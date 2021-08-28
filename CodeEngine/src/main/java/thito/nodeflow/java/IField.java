package thito.nodeflow.java;

public interface IField extends IMember {
    IClass getType();
    Reference get(Object instance);
    void set(Object instance, Object value);
}
