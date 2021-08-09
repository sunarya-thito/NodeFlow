package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;
import java.net.*;

public class ResourceURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        Resource resource = NodeFlow.getApplication().getResourceManager().getResource(u.getPath());
        if (resource instanceof ResourceFile) {
            return new ResourceURLConnection(u, (ResourceFile) resource);
        }
        throw new FileNotFoundException(u.getPath());
    }
}
