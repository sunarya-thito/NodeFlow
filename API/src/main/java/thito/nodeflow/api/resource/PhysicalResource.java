package thito.nodeflow.api.resource;

import java.nio.file.Path;

public interface PhysicalResource extends Resource {
    Path getSystemPath();
    boolean delete();
    boolean moveToRecycleBin();
    long getLastModified();
    void setLastModified(long lastModified);
    PhysicalResource moveTo(Resource path);
}
