package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;

public class FileResourceFileImpl extends FileResourceImpl implements ResourceFile {

    public FileResourceFileImpl(File file) {
        super(file);
    }

    @Override
    public String getFileName() {
        return getFile().getName();
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
            return new FileReader(getFile());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public InputStream openInput() {
        try {
            return new FileInputStream(getFile());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public byte[] readAll() {
        try (FileInputStream inputStream = new FileInputStream(getFile())) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024 * 8];
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }
}
