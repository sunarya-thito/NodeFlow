package thito.nodeflow.plugin;

import thito.nodeflow.language.*;
import thito.nodeflow.settings.*;
import thito.nodeflow.settings.canvas.*;
import thito.nodeflow.task.BatchTask;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

public class Plugin {

    public static Plugin getPlugin(Class<?> any) {
        if (any == null) return null;
        ClassLoader loader = any.getClassLoader();
        if (loader instanceof PluginClassLoader) {
            return ((PluginClassLoader) loader).getPlugin();
        }
        return null;
    }

    private String id;
    private String name;
    private String version;
    private Class<?> mainClass;
    private List<String> authors;
    private PluginInstance instance;
    private Logger logger;
    private File dataFolder;
    private PluginSettings pluginSettings;
    PluginClassLoader classLoader;

    public Plugin(String id, String name, String version, Class<?> mainClass, List<String> authors, PluginClassLoader pluginClassLoader, File dataFolder) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.mainClass = mainClass;
        this.authors = authors;
        this.classLoader = pluginClassLoader;
        this.logger = Logger.getLogger(name);
        this.dataFolder = dataFolder;
        this.pluginSettings = new PluginSettings(id, I18n.direct(name), SettingsContext.GLOBAL);
        Settings.getSettings().registerSettingsCategory(pluginSettings);
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getId() {
        return id;
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

    public PluginSettings getPluginSettings() {
        return pluginSettings;
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

    public BatchTask load() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        instance = (PluginInstance) mainClass.getConstructor().newInstance();
        return instance.createLoaderTask();
    }

    public BatchTask initialize() {
        BatchTask initializationTask = null;
        if (getPluginSettings().getEnable().getValue()) {
            initializationTask = instance.createInitializationTask();
        }
        pluginSettings.getEnable().valueProperty().addListener((obs, old, val) -> {
            BatchTask batchTask;
            if (val) {
                batchTask = instance.createInitializationTask();
            } else {
                batchTask = instance.createShutdownTask();
            }
            // TODO process the batch task
        });
        return initializationTask;
    }
}
