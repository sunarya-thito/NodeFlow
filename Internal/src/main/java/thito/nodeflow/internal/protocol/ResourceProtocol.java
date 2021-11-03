package thito.nodeflow.internal.protocol;

import thito.nodeflow.internal.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

public class ResourceProtocol extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        File file = new File(NodeFlow.RESOURCES_ROOT, URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8));
        return file.toURI().toURL().openConnection();
    }
}
