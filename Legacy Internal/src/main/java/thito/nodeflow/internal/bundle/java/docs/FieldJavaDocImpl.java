package thito.nodeflow.internal.bundle.java.docs;

import thito.nodeflow.api.bundle.java.docs.*;

public class FieldJavaDocImpl implements FieldJavaDoc {
    String name, description;

    public FieldJavaDocImpl(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
