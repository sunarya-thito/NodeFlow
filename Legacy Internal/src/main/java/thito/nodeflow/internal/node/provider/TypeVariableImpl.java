package thito.nodeflow.internal.node.provider;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class TypeVariableImpl<T extends GenericDeclaration> implements TypeVariable<T> {

    private String name;

    public TypeVariableImpl(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public Type[] getBounds() {
        return new Type[0];
    }

    @Override
    public T getGenericDeclaration() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        return new AnnotatedType[0];
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return new Annotation[0];
    }
}
