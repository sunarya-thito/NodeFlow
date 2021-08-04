package thito.nodeflow.api.resource;

import thito.nodeflow.api.ui.*;

import java.io.InputStream;
import java.util.Set;

public interface ResourceManager {
    Icon getIcon(String name);

    Image getImage(String name);

    Theme getTheme(String name);

    RandomAccessIcon loadIcon(InputStream inputStream);

    RandomAccessImage loadImage(InputStream inputStream);

    RandomAccessTheme loadTheme(InputStream inputStream);

    RandomAccessResourceFile createResourceFile(byte[] byteData);

    RandomAccessResourceFile getRandomAccessResourceFile(String name);

    Resource getResource(String name);

    Resource getExternalResource(String name);

    ResourceDirectory getBaseDirectory();

    ResourceWatcherService getWatcherService();

    void lockResource(ResourceFile file);

    void unlockResource(ResourceFile file);

    boolean isResourceLocked(ResourceFile file);

    Set<ResourceFile> getLockedResources();
}
