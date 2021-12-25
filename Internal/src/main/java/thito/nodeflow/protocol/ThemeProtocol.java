package thito.nodeflow.protocol;

import thito.nodeflow.NodeFlow;
import thito.nodeflow.plugin.Plugin;
import thito.nodeflow.plugin.PluginManager;
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
        String host = u.getHost();
        if (host != null) {
            Plugin plugin = PluginManager.getPluginManager().getPlugins().stream().filter(x -> x.getId().equals(host)).findAny().orElse(null);
            if (plugin != null) {
                String path = u.getPath();
                if (path.startsWith("/")) {
                    path = "Themes" + path;
                } else {
                    path = "Themes/" + path;
                }
                URL url = plugin.getClassLoader().getResource(path);
                if (url != null) {
                    return url.openConnection();
                }
            }
        }
        File file = new File(new File(NodeFlow.RESOURCES_ROOT, "Themes/" + ThemeManager.getInstance().getTheme().getName()), URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8));
        if (!file.exists()) NodeFlow.getLogger().log(Level.WARNING, "Cannot find file "+file+" for url "+u);
        return file.toURI().toURL().openConnection();
    }
}
