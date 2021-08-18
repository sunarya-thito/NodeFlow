package thito.nodeflow.internal.plugin;

import java.lang.reflect.*;
import java.util.*;

public class Plugin {

    public static Plugin getPlugin(Class<?> any) {
        if (any == null) return null;
        ClassLoader loader = any.getClassLoader();
        if (loader instanceof PluginClassLoader) {
            return ((PluginClassLoader) loader).getPlugin();
        }
        return null;
    }

    private String name;
    private String version;
    private Class<?> mainClass;
    private List<String> authors;
    private PluginInstance instance;
    PluginClassLoader classLoader;

    public Plugin(String name, String version, Class<?> mainClass, List<String> authors, PluginClassLoader pluginClassLoader) {
        this.name = name;
        this.version = version;
        this.mainClass = mainClass;
        this.authors = authors;
        this.classLoader = pluginClassLoader;
    }

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public PluginInstance getInstance() {
        if (instance == null) throw new IllegalStateException("not initialized");
        return instance;
    }

    public boolean isInitialized() {
        return instance != null;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    protected void initialize() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        instance = (PluginInstance) mainClass.getConstructor().newInstance();
    }
}
