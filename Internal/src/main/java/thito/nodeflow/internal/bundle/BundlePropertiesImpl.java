package thito.nodeflow.internal.bundle;

import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.resource.*;

public class BundlePropertiesImpl implements BundleProperties {

    private final BundleManagerImpl bundleManager;
    private final String id;
    private final String name;
    private final String author;
    private final String description;
    private final String version;
    private final ResourceDirectory directory;
    private final String javadoc;
    private final int jdVersion;

    public BundlePropertiesImpl(BundleManagerImpl bundleManager, String id, String name, String author, String description, String version, String javadoc, int jdVersion, ResourceDirectory directory) {
        this.id = id;
        this.bundleManager = bundleManager;
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.directory = directory;
        this.javadoc = javadoc;
        this.jdVersion = jdVersion;
    }

    @Override
    public int getJavaDocVersion() {
        return jdVersion;
    }

    @Override
    public String getJavaDoc() {
        return javadoc;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ResourceDirectory getDirectory() {
        return directory;
    }

    @Override
    public Bundle loadBundle() {
        return bundleManager.loadBundle(this);
    }
}
