package thito.nodeflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;
import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.language.Language;
import thito.nodeflow.plugin.Plugin;
import thito.nodeflow.plugin.PluginClassLoader;
import thito.nodeflow.project.Tag;
import thito.nodeflow.project.Workspace;
import thito.nodeflow.resource.ResourceWatcher;
import thito.nodeflow.settings.Settings;
import thito.nodeflow.task.BatchTask;
import thito.nodeflow.task.TaskManager;
import thito.nodeflow.ui.Theme;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeFlow {

    private static NodeFlow instance;
    public static final File ROOT;
    public static final File RESOURCES_ROOT;

    static {
        String rootProp = System.getProperty("nodeflow.rootDirectory", "");
        String resourcesRootProp = System.getProperty("nodeflow.resourcesRootDirectory", "");
        if (!new File(rootProp).exists()) {
            rootProp = "";
        }
        if (!new File(resourcesRootProp).exists()) {
            resourcesRootProp = "";
        }
        ROOT = new File(rootProp).getAbsoluteFile();
        RESOURCES_ROOT = new File(resourcesRootProp).getAbsoluteFile();
    }

    private static final Logger logger = Logger.getLogger("NodeFlow");

    public static Logger getLogger() {
        return logger;
    }


    public static NodeFlow getInstance() {
        return instance;
    }

    private Language defaultLanguage;
    private ObjectProperty<Workspace> workspace = new SimpleObjectProperty<>();
    private ObservableMap<String, Tag> tagMap = FXCollections.observableHashMap();

    @BGThread
    public NodeFlow() {
        if (instance != null) throw new IllegalStateException("already initiated");
        instance = this;
        URL.setURLStreamHandlerFactory(this::getProtocolHandler);
    }

    protected BatchTask createInitializationTasks() {
        BatchTask batchTask = new BatchTask();
        File pluginDirectory = new File(ROOT, "Plugins");
        pluginDirectory.mkdirs();
        File[] pluginFiles = pluginDirectory.listFiles();
        if (pluginFiles != null) {
            ArrayList<Plugin> initialized = new ArrayList<>();
            batchTask.submitTask(progress -> {
                progress.setStatus("Loading plugins");
                BatchTask task = new BatchTask();
                for (File f : pluginFiles) {
                    if (!f.getName().endsWith(".jar")) continue;
                    getLogger().log(Level.INFO, "Loading " + f.getName());
                    try {
                        PluginClassLoader pluginClassLoader = new PluginClassLoader(f, getClass().getClassLoader());
                        pluginClassLoader.load();
                        Plugin plugin = pluginClassLoader.getPlugin();
                        task.submitTask(plugin.load());
                        initialized.add(plugin);
                    } catch (Throwable t) {
                        getLogger().log(Level.SEVERE, "Failed to load plugin "+f.getName(), t);
                    }
                }
                task.run(progress);
            });
            for (Plugin p : initialized) {
                getLogger().log(Level.INFO, "Initializing "+p.getName());
                batchTask.submitTask(p.initialize());
            }
        }
        return batchTask;
    }

    private Map<String, URLStreamHandler> protocolHandlerMap = new HashMap<>();

    public void registerProtocol(String protocol, URLStreamHandler handler) {
        protocolHandlerMap.put(protocol, handler);
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    private URLStreamHandler getProtocolHandler(String protocol) {
        return protocolHandlerMap.get(protocol);
    }

    public Section readFromConfiguration() {
        File file = new File(ROOT, "config.yml");
        if (!file.exists()) return new MapSection();
        try (FileReader reader = new FileReader(file)) {
            return Section.parseToMap(reader);
        } catch (IOException t) {
            throw new RuntimeException(t);
        }
    }

    public void saveToConfiguration(Section section) {
        try (FileWriter writer = new FileWriter(new File(ROOT, "config.yml"))) {
            writer.write(Section.toString(section));
        } catch (IOException t) {
            throw new RuntimeException(t);
        }
    }

    public void registerTag(String id, Tag tag) {
        tagMap.put(id, tag);
    }

    public void unregisterTag(String id, Tag tag) {
        tagMap.remove(id, tag);
    }

    public ObservableMap<String, Tag> getTagMap() {
        return tagMap;
    }

    public Tag getTag(String id) {
        return tagMap.get(id);
    }

    public ObjectProperty<Workspace> workspaceProperty() {
        return workspace;
    }

    public void shutdown() {
        ResourceWatcher.getResourceWatcher().close();
        TaskManager.getInstance().shutdown();
        Settings.getSettings().saveGlobalConfiguration();
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public Language getLanguage(String code) {
        return getAvailableLanguages().stream().filter(l -> l.getCode().equals(code)).findAny().orElseGet(() -> {
            Language language = new Language(code);
            cached.add(language);
            return language;
        });
    }

    public Collection<? extends Theme> getAvailableThemes() {
        List<Theme> themes = new ArrayList<>();
        File[] list = new File(RESOURCES_ROOT, "Themes").listFiles();
        if (list != null) {
            for (File f : list) {
                themes.add(new Theme(f.getName()));
            }
        }
        return themes;
    }

    private List<Language> cached;
    public Collection<Language> getAvailableLanguages() {
        if (cached != null) return cached;
        List<Language> languages = new ArrayList<>();
        File[] list = new File(RESOURCES_ROOT, "Locales").listFiles();
        if (list != null) {
            for (File f : list) {
                Language l = new Language(f.getName().replace(".yml", ""));
                try (FileReader reader = new FileReader(f)) {
                    l.loadLanguage(reader);
                    languages.add(l);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return cached = languages;
    }
}
