package thito.nodeflow.java;

import java.util.*;

public abstract class AbstractMember implements IMember {
    private final String name;
    private final IClass declaringClass;
    protected int modifiers;

    public AbstractMember(String name, IClass declaringClass) {
        this.name = name;
        this.declaringClass = declaringClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public IClass getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof IMember) {
            return ((IMember) obj).getName().equals(getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
