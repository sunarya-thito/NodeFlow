package thito.nodeflow.internal.plugin;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.resource.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

public class PluginClassLoader extends URLClassLoader {
    private Plugin plugin;
    private File file;
    public PluginClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        super(new URL[] { file.toURI().toURL() }, parent);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Plugin getPlugin() {
        if (plugin == null) throw new IllegalStateException("not loaded");
        return plugin;
    }

    public boolean isLoaded() {
        return plugin != null;
    }

    public void load() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(getResourceAsStream("plugin.yml")))) {
            Section pluginProperties = Section.parseToMap(reader);
            String id = pluginProperties.getString("id").orElseThrow();
            String name = pluginProperties.getString("name").orElseThrow();
            String version = pluginProperties.getString("version").orElseThrow();
            String main = pluginProperties.getString("main").orElseThrow();
            List<String> authors = pluginProperties.getList("authors").orElse(new ListSection())
                    .stream().map(String::valueOf).collect(Collectors.toList());
            Class<?> mainClass = Class.forName(main, true, this);
            if (!PluginInstance.class.isAssignableFrom(mainClass)) throw new ClassNotFoundException("main class "+mainClass.getName()+" does not implement "+PluginInstance.class.getName());
            File dataFolder = new File(file.getParentFile(), id);
            dataFolder.mkdirs();
            plugin = new Plugin(id, name, version, mainClass, authors, this, dataFolder);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
