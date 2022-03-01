package thito.nodeflow.protocol;

import thito.nodeflow.NodeFlow;
import thito.nodeflow.ui.ThemeManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ThemeProtocol extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        File file = new File(new File(NodeFlow.RESOURCES_ROOT, "Themes/" + ThemeManager.getInstance().getTheme().getName()), URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8));
        if (!file.exists()) NodeFlow.getLogger().log(Level.WARNING, "Cannot find file "+file+" for url "+u);
        return file.toURI().toURL().openConnection();
    }
}
