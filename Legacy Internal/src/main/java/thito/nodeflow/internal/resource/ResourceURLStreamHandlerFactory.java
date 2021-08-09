package thito.nodeflow.internal.resource;

import java.net.*;

public class ResourceURLStreamHandlerFactory implements URLStreamHandlerFactory {
    public static final String PROTOCOL = "rsrc";
    private final ResourceURLStreamHandler handler;

    public ResourceURLStreamHandlerFactory(ResourceURLStreamHandler handler) {
        this.handler = handler;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL.equals(protocol)) {
            return handler;
        }
        return null;
    }
}
