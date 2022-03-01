package thito.nodeflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;
import thito.nodeflow.language.Language;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.project.Tag;
import thito.nodeflow.project.Workspace;
import thito.nodeflow.project.module.UnknownFileModule;
import thito.nodeflow.resource.ResourceWatcher;
import thito.nodeflow.settings.Settings;
import thito.nodeflow.task.TaskManager;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.ui.Theme;
import thito.nodeflow.ui.docker.DockerManager;
import thito.nodeflow.ui.editor.docker.ProjectStructureComponent;
import thito.nodeflow.ui.editor.docker.WelcomePageComponent;

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

    public NodeFlow() {
        if (instance != null) throw new IllegalStateException("already initiated");
        instance = this;
        URL.setURLStreamHandlerFactory(this::getProtocolHandler);
    }

    protected Batch.Task createInitializationTasks() {
        return Batch
                .execute(TaskThread.BG(), progress -> {
                    progress.setStatus("Registering editor components");
                    DockerManager manager = DockerManager.getManager();
                    manager.registerDockerComponent(new ProjectStructureComponent());
                    manager.registerDockerComponent(new WelcomePageComponent());
                    manager.registerDockerComponent(ProjectManager.getInstance().getFileViewerComponent());
                })
                .execute(TaskThread.BG(), progress -> {
                    progress.setStatus("Registering modules");
                    ProjectManager.getInstance().getModuleList().add(new UnknownFileModule());
//                    PluginManager pluginManager = PluginManager.getPluginManager();
//                    pluginManager.registerFileModule(new DirectoryFileModule());
//                    pluginManager.registerFileModule(new UnknownFileModule());
                })
                .execute(TaskThread.IO(), progress -> {
                    progress.setStatus("Loading plugins");
                    File pluginDirectory = new File(ROOT, "Plugins");
                    pluginDirectory.mkdirs();
                    File[] pluginFiles = pluginDirectory.listFiles();
                    if (pluginFiles != null && pluginFiles.length > 0) {
                        for (File f : pluginFiles) {
                            progress.append(TaskThread.IO(), pr -> {
                                if (f.getName().endsWith(".jar")) {
                                    pr.setStatus("Loading " + f.getName());
//                                    try {
//                                        PluginClassLoader pluginClassLoader = new PluginClassLoader(f, getClass().getClassLoader());
//                                        pluginClassLoader.load();
//                                        Plugin plugin = pluginClassLoader.getPlugin();
//                                        pr.insert(plugin.load());
//                                        pr.append(TaskThread.BG(), prx -> {
//                                            PluginManager.getPluginManager().registerPlugin(plugin);
//                                            pr.setStatus("Initializing "+plugin.getName());
//                                            prx.append(plugin.initialize());
//                                        });
//                                    } catch (Throwable t) {
//                                        getLogger().log(Level.SEVERE, "Failed to load plugin "+f.getName(), t);
//                                    }
                                }
                            });
                        }
                    }
                });
    }

    private Map<String, URLStreamHandler> protocolHandlerMap = new HashMap<>();

    public void registerProtocol(String protocol, URLStreamHandler handler) {
        getLogger().log(Level.INFO, "Registering protocol "+protocol+" for "+handler);
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
        getLogger().log(Level.INFO, "Shutting down application...");
        ResourceWatcher.getResourceWatcher().close();
        Settings.getSettings().saveGlobalConfiguration().execute(TaskThread.BG(), pr -> {
            getLogger().log(Level.INFO, "Terminating tasks...");
            TaskManager.getInstance().shutdown();
            getLogger().log(Level.INFO, "Good bye!");
            System.exit(0);
        }).start();
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
