package thito.nodeflow.internal.bundle.java.oracle;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.resource.*;

public class JREBundleProperties implements BundleProperties {
    @Override
    public String getId() {
        return "oracle.javaRuntimeEnvironment";
    }

    @Override
    public String getName() {
        return "JRE Bundle";
    }

    @Override
    public String getAuthor() {
        return "Oracle";
    }

    @Override
    public String getDescription() {
        return "Java";
    }

    @Override
    public String getVersion() {
        return "8";
    }

    @Override
    public String getJavaDoc() {
        return null;
    }

    @Override
    public int getJavaDocVersion() {
        return 0;
    }

    @Override
    public ResourceDirectory getDirectory() {
        return (ResourceDirectory) ResourceManagerImpl.fileToResource(ResourceManagerImpl.BASE_DIRECTORY);
    }

    @Override
    public Bundle loadBundle() {
        JREBundle bundle;
        ((BundleManagerImpl) NodeFlow.getApplication().getBundleManager()).addBundle(bundle = new JREBundle(this, NodeFlow.getApplication().getBundleManager()));
        return bundle;
    }
}
