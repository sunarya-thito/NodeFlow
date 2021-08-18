package thito.nodeflow.internal.protocol;

import thito.nodeflow.internal.plugin.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

public class PluginResourceProtocol extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        Class<?> caller = null;
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (e.getClassName().equals(PluginResourceProtocol.class.getName()) ||
            e.getClassName().equals(URL.class.getName())) continue;
            try {
                caller = Class.forName(e.getClassName());
                break;
            } catch (ClassNotFoundException classNotFoundException) {
            }
        }
        Plugin plugin = Plugin.getPlugin(caller);
        if (plugin == null) throw new IOException("not inside plugin scope");
        return new URLConnection(u) {
            private boolean connected;
            private InputStream inputStream;

            @Override
            public void connect() throws IOException {
                if (!connected) {
                    connected = true;
                    String path = URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8);
                    InputStream stream = plugin.getClassLoader().getResourceAsStream(path);
                    if (stream == null) throw new FileNotFoundException(path);
                    inputStream = new FilterInputStream(stream) {
                        @Override
                        public void close() throws IOException {
                            super.close();
                            connected = false;
                            inputStream = null;
                        }
                    };
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
