package thito.nodeflow.javadoc;

import java.util.*;
import java.util.stream.*;

public class JavaAnnotation {
    private String type;
    private Value[] values;

    public void setType(String type) {
        this.type = type;
    }

    public void setValues(Value[] values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        if (values != null && values.length > 0) {
            return "@" + type.toString() + "(" + Arrays.stream(values).map(Object::toString).collect(Collectors.joining(","))+")";
        }
        return "@" + type.toString();
    }

    public Value[] getValues() {
        return values;
    }

    public static class Value {
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

        public String toString() {
            if (value instanceof Object[]) {
                return name + " = " + Arrays.toString((Object[]) value);
            }
            return name + " = " + value;
        }
    }
}
