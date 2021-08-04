package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

import java.io.*;
import java.net.*;

public class ResourceURLConnection extends URLConnection {
    private final ResourceFile resourceFile;
    private ByteArrayInputStream byteArrayInputStream;
    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected ResourceURLConnection(URL url, ResourceFile file) {
        super(url);
        resourceFile = file;
    }

    private InputStream _getInputStream() {
        if (byteArrayInputStream == null) connect();
        return byteArrayInputStream;
    }

    @Override
    public void connect() {
        byteArrayInputStream = new ByteArrayInputStream(resourceFile.readAll());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return _getInputStream();
    }

}
