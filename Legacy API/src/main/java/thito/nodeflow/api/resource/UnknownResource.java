package thito.nodeflow.api.resource;

public interface UnknownResource extends ResourceDirectory {
    ResourceFile createFile();

    ResourceDirectory createDirectory();

    Resource getChild(String path);
}
