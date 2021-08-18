package thito.nodeflow.internal.protocol;

import thito.nodeflow.internal.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

public class ResourceProtocol extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        File file = new File(NodeFlow.ROOT, URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8));
        return new URLConnection(u) {

            private InputStream inputStream;
            @Override
            public void connect() throws IOException {
                if (!connected) {
                    inputStream = new FileInputStream(file) {
                        @Override
                        public void close() throws IOException {
                            super.close();
                            connected = false;
                            inputStream = null;
                        }
                    };
                    connected = true;
                }
            }

            @Override
            public InputStream getInputStream() throws IOException {
                connect();
                return inputStream;
            }

        };
    }
}
