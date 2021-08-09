package thito.nodeflow.api.bundle;

import thito.nodeflow.api.resource.ResourceDirectory;

public interface BundleProperties {
    String getId();

    String getName();

    String getAuthor();

    String getDescription();

    String getVersion();

    String getJavaDoc();

    int getJavaDocVersion();

    ResourceDirectory getDirectory();

    Bundle loadBundle();
}
