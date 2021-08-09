package thito.nodeflow.internal.bundle.java.docs;

import thito.nodeflow.api.bundle.java.docs.*;

public class PackageJavaDocImpl implements PackageJavaDoc {
    private final String packageName;
    private final String description;

    public PackageJavaDocImpl(String packageName, String description) {
        this.packageName = packageName;
        this.description = description;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
