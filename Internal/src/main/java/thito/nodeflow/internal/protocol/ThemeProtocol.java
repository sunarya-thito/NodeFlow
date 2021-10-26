package thito.nodeflow.internal.protocol;

import thito.nodeflow.internal.NodeFlow;
import thito.nodeflow.internal.ui.ThemeManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

public class ThemeProtocol extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        File file = new File(new File(NodeFlow.RESOURCES_ROOT, "Themes/" + ThemeManager.getInstance().getTheme().getName()), URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8));
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
