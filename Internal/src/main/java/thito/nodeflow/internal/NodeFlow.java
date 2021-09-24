package thito.nodeflow.internal;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.config.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.application.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.internal.application.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.task.*;
import thito.nodeflow.internal.ui.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class NodeFlow extends ApplicationResources {

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
        return (NodeFlow) ApplicationResources.getInstance();
    }

    private Language defaultLanguage;
    protected LinkedList<ProgressedTask> progressedTasks = new LinkedList<>();
    private ObjectProperty<Workspace> workspace = new SimpleObjectProperty<>();
    private ObservableMap<String, Tag> tagMap = FXCollections.observableHashMap();
    private ObservableList<Editor> activeEditors = FXCollections.observableArrayList();

    public NodeFlow() {
        getAvailableLanguages();
        Language.setLanguage(defaultLanguage = getLanguage("en_us"));
        activeEditors.addListener((InvalidationListener) obs -> {
            if (activeEditors.isEmpty()) {
                shutdown();
            }
        });
        File pluginDirectory = new File(ROOT, "Plugins");
        pluginDirectory.mkdirs();
        File[] pluginFiles = pluginDirectory.listFiles();
        if (pluginFiles != null) {
            List<Plugin> initialized = new ArrayList<>();
            for (int i = 0; i < pluginFiles.length; i++) {
                File f = pluginFiles[i];
                if (!f.getName().endsWith(".jar")) continue;
                double progressValue = i / (double) pluginFiles.length;
                submitProgressedTask((progress, status) -> {
                    status.set("Loading plugin "+f.getName());
                    try {
                        Plugin plugin = PluginManager.getPluginManager().loadPlugin(f);
                        getLogger().log(Level.INFO, "Loading "+plugin.getName()+" ("+f.getName()+")");
                        initialized.add(plugin);
                    } catch (Throwable t) {
                        getLogger().log(Level.SEVERE, "Failed to load "+f.getName(), t);
                    }
                    progress.set(progressValue);
                });
            }
            submitProgressedTask((progress, status) -> {
                status.set("Launching plugins...");
                progress.set(0);
                for (int i = 0; i < initialized.size(); i++) {
                    Plugin plugin = initialized.get(i);
                    double pg = i / (double) initialized.size();
                    submitProgressedTask((p2, s2) -> {
                        s2.set("Initializing "+plugin.getName());
                        getLogger().log(Level.INFO, "Initializing "+plugin.getName());
                        try {
                            plugin.launch();
                        } catch (Throwable t) {
                            getLogger().log(Level.SEVERE, "Failed to launch "+plugin.getName(), t);
                        }
                        p2.set(pg);
                    });
                }
            });
        }
        getAvailableLanguages();
        workspace.addListener((obs, old, val) -> {
            if (old != null) {
                TaskThread.BACKGROUND().schedule(() -> {
                    for (ProjectProperties properties : old.getProjectPropertiesList()) {
                        properties.closeProject();
                    }
                });
            }
        });
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

    public Editor createNewEditor() {
        TaskThread.UI().checkThread();
        Editor editor = new Editor();
        editor.getEditorWindow().show();
        return editor;
    }

    public ObservableList<Editor> getActiveEditors() {
        return activeEditors;
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

    public void submitProgressedTask(ProgressedTask task) {
        if (progressedTasks == null) throw new IllegalStateException("nodeflow is already loaded");
        progressedTasks.add(task);
    }

    public void shutdown() {
        ResourceWatcher.getResourceWatcher().close();
        TaskManager.getInstance().shutdown();
    }

    @Override
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

    @Override
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
    @Override
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
