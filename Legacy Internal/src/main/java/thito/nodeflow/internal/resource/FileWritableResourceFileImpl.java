package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;

public class FileWritableResourceFileImpl extends FileResourceFileImpl implements WritableResourceFile {

    public FileWritableResourceFileImpl(File file) {
        super(file);
    }

    @Override
    public Writer openWriter() {
        try {
            return new FileWriter(getFile());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public OutputStream openOutput() {
        try {
            return new FileOutputStream(getFile());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public void writeAll(byte[] bytes) {
        try (FileOutputStream outputStream = new FileOutputStream(getFile())) {
            outputStream.write(bytes);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

}
