package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

import java.io.*;
import java.nio.charset.*;

public class RandomAccessResourceFileImpl implements RandomAccessResourceFile {

    private static String toString(long mostSigBits, long leastSigBits) {
        return (digits(mostSigBits >> 32, 8) + "-" +
                digits(mostSigBits >> 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits >> 48, 4) + "-" +
                digits(leastSigBits, 12));
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
    private final long id;
    private byte[] byteData;
    private final ResourceManagerImpl resourceManager;

    public RandomAccessResourceFileImpl(long id, byte[] byteData, ResourceManagerImpl resourceManager) {
        this.id = id;
        this.byteData = byteData;
        this.resourceManager = resourceManager;
    }

    @Override
    public void dispose() {
        byteData = null;
        resourceManager.closeRandomAccessResourceFile(this);
    }

    @Override
    public String getFileName() {
        return getName()+".tmp";
    }

    @Override
    public String getExtension() {
        return "tmp";
    }

    @Override
    public Reader openReader() {
        return new InputStreamReader(openInput(), StandardCharsets.UTF_8);
    }

    @Override
    public InputStream openInput() {
        if (byteData == null) throw new IllegalStateException("closed");
        return new ByteArrayInputStream(byteData);
    }

    @Override
    public byte[] readAll() {
        return byteData.clone();
    }

    @Override
    public ResourceDirectory getParentDirectory() {
        return null;
    }

    @Override
    public String getPath() {
        return getFileName();
    }

    @Override
    public String getName() {
        return toString(id, id);
    }
}
