package thito.nodeflow.internal.bundle.java.docs;

import thito.nodeflow.api.bundle.java.docs.*;

public class MethodJavaDocImpl implements MethodJavaDoc {
    private final String name;
    private final String description;

    public MethodJavaDocImpl(String name, String description) {
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
