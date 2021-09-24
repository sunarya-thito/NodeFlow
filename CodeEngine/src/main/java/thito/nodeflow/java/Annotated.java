package thito.nodeflow.java;

import thito.nodeflow.java.util.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class Annotated {

    public static Annotated annotation(Annotation annotation) {
        Annotated annotated = annotation(annotation.annotationType());
        for (Method method : annotated.getClass().getDeclaredMethods()) {
            try {
                annotated.value(method.getName(), method.invoke(annotation));
            } catch (Throwable t) {
            }
        }
        return annotated;
    }

    public static Annotated annotation(Class<? extends Annotation> type) {
        return annotation(Java.Class(type));
    }

    public static Annotated annotation(IClass type) {
        return new Annotated(type);
    }

    private IClass type;
    private List<Value> values = new ArrayList<>();

    public Annotated(IClass type) {
        this.type = type;
    }

    public List<Value> getValues() {
        return values;
    }

    public IClass getType() {
        return type;
    }

    public boolean isVisible() {
        Annotated annotated = type.getAnnotation(Java.Class(Retention.class));
        Value value = get("value").orElse(null);
        if (value != null) {
            Object val = value.getValue();
            if (val instanceof Enum) {
                return val == RetentionPolicy.RUNTIME || val == RetentionPolicy.CLASS;
            }
        }
        return false;
    }

    public Optional<Value> get(String name) {
        return values.stream().filter(x -> x.getName().equals(name)).findAny();
    }

    public Annotated value(String name, Object value) {
        values.add(new Value(name, value));
        return this;
    }

    public class Value {
        private String name;
        private Object value;

        public Value(String name, Object value) {
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
}
