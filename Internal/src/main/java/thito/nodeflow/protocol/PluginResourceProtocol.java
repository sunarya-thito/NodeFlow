package thito.nodeflow.protocol;

import thito.nodeflow.plugin.Plugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

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
        URL url = plugin.getClassLoader().getResource(u.getPath());
        if (url == null) throw new FileNotFoundException(u.getPath());
        return url.openConnection();
    }
}
