package thito.nodeflow.plugin;

import thito.nodeflow.language.I18n;
import thito.nodeflow.settings.PluginSettings;
import thito.nodeflow.settings.Settings;
import thito.nodeflow.settings.canvas.SettingsContext;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

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

    public Batch.Task load() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        return Batch.execute(TaskThread.BG(), progress -> {
            progress.setStatus("Creating main class instance");
            instance = (PluginInstance) mainClass.getConstructor().newInstance();
        }).execute(TaskThread.BG(), pr -> pr.append(instance.createLoaderTask()));
    }

    public Batch.Task shutdown() {
        return instance.createShutdownTask();
    }

    public Batch.Task initialize() {
        return instance.createInitializationTask();
    }
}
