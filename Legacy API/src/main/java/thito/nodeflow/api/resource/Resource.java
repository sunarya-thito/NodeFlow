package thito.nodeflow.api.resource;

import thito.nodeflow.api.*;

public interface Resource {
    ResourceDirectory getParentDirectory();

    String getPath(); // Absolute path!

    String getName(); // without extension!

    default Resource reload() {
        return NodeFlow.getApplication().getResourceManager().getResource(getPath());
    }
}
