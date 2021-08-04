package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ResourceManagerImpl implements ResourceManager {

    public static File BASE_DIRECTORY = new File("").getAbsoluteFile();
    public static Resource fileToResource(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                return new FileResourceDirectoryImpl(file);
            } else {
                if (file.canWrite()) {
                    return new FileWritableResourceFileImpl(file);
                } else {
                    return new FileResourceFileImpl(file);
                }
            }
        } else {
            return new FileUnknownResourceImpl(file);
        }
    }

    public static Resource resourceToResource(String name, InputStream inputStream, ResourceDirectory parent) {
        if (inputStream == null) return new InnerUnknownResourceImpl(name, parent);
        if (inputStream instanceof ByteArrayInputStream) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buff = new byte[1024 * 8];
            try {
                while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                    outputStream.write(buff, 0, len);
                }
            } catch (Throwable t) {
                throw new ReportedError(t);
            }
            String[] paths = outputStream.toString().split("\n");
            return new InnerResourceDirectoryImpl(paths, name, parent);
        }
        return new InnerResourceFileImpl(name, parent);
    }

    private ResourceDirectory baseDirectory = (ResourceDirectory) fileToResource(BASE_DIRECTORY);
    private final ResourceWatcherService watcherService = new ResourceWatcherServiceImpl();
    private final Map<ResourceFile, FileLock> lockedResources = new ConcurrentHashMap<>();
    private final Map<String, Icon> cachedIcons = new ConcurrentHashMap<>();
    private final Map<String, Image> cachedImages = new ConcurrentHashMap<>();
    private final Map<String, Theme> cachedThemes = new ConcurrentHashMap<>();
    private final Map<String, RandomAccessResourceFile> cachedRARFile = new ConcurrentHashMap<>();
    private final RandomAccessResourceFile unknownRARFile = createResourceFile(new byte[0]);
    private long lastRARid = 0;

    public void setBaseDirectory(File file) {
        baseDirectory = (ResourceDirectory) fileToResource(file);
    }

    public ResourceManagerImpl() {
        LinkedURLStreamHandlerFactory.registerURLStreamHandlerFactory(new ResourceURLStreamHandlerFactory(new ResourceURLStreamHandler()));
    }

    public void closeRandomAccessResourceFile(RandomAccessResourceFile resourceFile) {
        cachedRARFile.remove(resourceFile.getName(), resourceFile);
    }

    @Override
    public RandomAccessResourceFile createResourceFile(byte[] byteData) {
        RandomAccessResourceFileImpl rar = new RandomAccessResourceFileImpl(lastRARid++, byteData, this);
        cachedRARFile.put(rar.getName(), rar);
        return rar;
    }

    @Override
    public synchronized void lockResource(ResourceFile file) {
        if (file instanceof PhysicalResource) {
            try {
                FileChannel channel = FileChannel.open(((PhysicalResource) file).getSystemPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
                FileLock lock = channel.lock();
                lockedResources.put(file, lock);
            } catch (IOException e) {
            }
        }
    }

    @Override
    public synchronized void unlockResource(ResourceFile file) {
        try {
            FileLock lock = lockedResources.get(file);
            if (lock != null) {
                lock.release();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public synchronized boolean isResourceLocked(ResourceFile file) {
        return lockedResources.containsKey(file);
    }

    @Override
    public Set<ResourceFile> getLockedResources() {
        return lockedResources.keySet();
    }

    @Override
    public Icon getIcon(String name) {
        return cachedIcons.computeIfAbsent(name, x -> new IconImpl(name, NodeFlow.getApplication().getUIManager().getTheme()));
    }

    @Override
    public Image getImage(String name) {
        return cachedImages.computeIfAbsent(name, x -> new ImageImpl((ResourceFile) getResource("images/"+name+".png")));
    }

    @Override
    public Theme getTheme(String name) {
        return cachedThemes.computeIfAbsent(name, ThemeImpl::new);
    }

    @Override
    public RandomAccessIcon loadIcon(InputStream inputStream) {
        return new StaticIcon(inputStream);
    }

    @Override
    public RandomAccessImage loadImage(InputStream inputStream) {
        return new StaticIcon(inputStream);
    }

    @Override
    public RandomAccessTheme loadTheme(InputStream inputStream) {
        RandomAccessResourceFile randomAccessResourceFile = createResourceFile(Toolkit.reportErrorLater(() -> Toolkit.readAll(inputStream)));
        return new RandomAccessThemeImpl(randomAccessResourceFile.getName(), randomAccessResourceFile);
    }

    @Override
    public RandomAccessResourceFile getRandomAccessResourceFile(String name) {
        return cachedRARFile.getOrDefault(name, unknownRARFile);
    }

    @Override
    public Resource getResource(String name) {
        ResourceFile rarFile = getRandomAccessResourceFile(name);
        if (rarFile != unknownRARFile) {
            return rarFile;
        }

        Resource child = getBaseDirectory().getChild(name);
        if (!(child instanceof UnknownResource)) {
            return child;
        }

        child = fileToResource(new File(baseDirectory.getPath(), name));

        if (!(child instanceof UnknownResource)) {
            return child;
        }

        try (InputStream check = getClass().getClassLoader().getResourceAsStream(name)) {
            return resourceToResource(name, check, null);
        } catch (Throwable t) {
        }

        return child;
    }

    @Override
    public Resource getExternalResource(String name) {
        Resource child = getBaseDirectory().getChild(name);
        if (!(child instanceof UnknownResource)) {
            return child;
        }

        child = fileToResource(new File(baseDirectory.getPath(), name));
        return child;
    }

    @Override
    public ResourceDirectory getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public ResourceWatcherService getWatcherService() {
        return watcherService;
    }
}
