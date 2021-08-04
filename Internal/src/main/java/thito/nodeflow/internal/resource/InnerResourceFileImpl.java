package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;
import java.util.*;

public class InnerResourceFileImpl implements ResourceFile {
    private final String fileName;
    private final ResourceDirectory parent;

    public InnerResourceFileImpl(String fileName, ResourceDirectory parent) {
        this.fileName = fileName;
        this.parent = parent;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getExtension() {
        String fileName = getFileName();
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index + 1) : "";
    }

    @Override
    public Reader openReader() {
        try {
            return new InputStreamReader(openInput());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public InputStream openInput() {
        try {
            return getClass().getClassLoader().getResourceAsStream(getPath());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public byte[] readAll() {
        try (InputStream inputStream = openInput()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buff = new byte[1024 * 8];
            while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                outputStream.write(buff, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public ResourceDirectory getParentDirectory() {
        return parent;
    }

    @Override
    public String getPath() {
        return parent != null ? parent.getPath() + File.separator + getFileName() : getFileName();
    }

    @Override
    public String getName() {
        String fileName = getFileName();
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(0, index) : fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnerResourceFileImpl that = (InnerResourceFileImpl) o;

        return Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
