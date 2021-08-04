package thito.nodeflow.api.resource;

public interface ResourceDirectory extends Resource {
    Resource[] getChildren();

    Resource getChild(String path);

    default Resource getOrCreateChildDirectory(String path) {
        Resource resource = getChild(path);
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createDirectory();
        }
        return resource;
    }

    default Resource getOrCreateChildFile(String path) {
        Resource resource = getChild(path);
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        return resource;
    }
}
