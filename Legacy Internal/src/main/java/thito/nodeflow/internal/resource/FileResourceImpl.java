package thito.nodeflow.internal.resource;

import com.sun.jna.platform.*;
import thito.nodeflow.api.resource.*;

import java.io.*;
import java.nio.file.*;

public class FileResourceImpl implements PhysicalResource {

    private final File file;
    private ResourceDirectory directory;

    public FileResourceImpl(File file) {
        this.file = file;
    }

    @Override
    public Path getSystemPath() {
        return file.toPath();
    }

    public File getFile() {
        File absolute = file.getAbsoluteFile();
        return absolute == null ? file : absolute;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public void setLastModified(long lastModified) {
        file.setLastModified(lastModified);
    }

    @Override
    public boolean moveToRecycleBin() {
        if (FileUtils.getInstance().hasTrash()) {
            try {
                trash(file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void trash(File file) throws IOException {
        File[] list = file.listFiles();
        if (list != null) {
            for (File f : list) {
                trash(f);
            }
            file.delete();
            return;
        }
        if (file.exists()) {
            FileUtils.getInstance().moveToTrash(new File[] {file});
        }
    }

    @Override
    public boolean delete() {
        return file.delete();
    }

    @Override
    public ResourceDirectory getParentDirectory() {
        if (directory == null) {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                directory = (ResourceDirectory) ResourceManagerImpl.fileToResource(parentFile);
            }
        }
        return directory;
    }

    @Override
    public String getPath() {
        String absolute = file.getAbsolutePath();
        return absolute == null ? file.getPath() : absolute;
    }

    @Override
    public String getName() {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(0, index) : fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        return getPath().equalsIgnoreCase(((Resource) o).getPath());
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public PhysicalResource moveTo(Resource path) {
        File fx = new File(path.getPath());
        File parent = fx.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!file.renameTo(fx)) throw new IllegalStateException("unable to move file "+getPath());
        return (PhysicalResource) ResourceManagerImpl.fileToResource(fx);
    }
}
