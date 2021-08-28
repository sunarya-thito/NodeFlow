package thito.nodeflow.internal;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.application.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class NodeFlow extends ApplicationResources {

    public static final File ROOT;

    static {
        String rootProp = System.getProperty("nodeflow.rootDirectory", "");
        ROOT = new File(rootProp).getAbsoluteFile();
    }

    private static final Logger logger = Logger.getLogger("NodeFlow");

    public static Logger getLogger() {
        return logger;
    }


    public static NodeFlow getInstance() {
        return (NodeFlow) ApplicationResources.getInstance();
    }

    private Language defaultLanguage = new Language("en_us");
    private SettingsManager settingsManager = new SettingsManager();
    protected LinkedList<ProgressedTask> progressedTasks = new LinkedList<>();
    private ObjectProperty<Workspace> workspace = new SimpleObjectProperty<>();
    private ObservableMap<String, Tag> tagMap = FXCollections.observableHashMap();
    private ObservableList<Editor> activeEditors = FXCollections.observableArrayList();

    public NodeFlow() {
        try (FileReader reader = new FileReader(new File(ROOT, "Locales/en_us.yml"))) {
            defaultLanguage.loadLanguage(reader);
            Language.setLanguage(defaultLanguage);
        } catch (Throwable t) {
            throw new RuntimeException("failed to load default language (en_us.yml)", t);
        }
        activeEditors.addListener((InvalidationListener) obs -> {
            if (activeEditors.isEmpty()) {
                shutdown();
            }
        });
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

    public SettingsManager getSettingsManager() {
        return settingsManager;
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

    @Override
    public Collection<? extends Theme> getAvailableThemes() {
        List<Theme> themes = new ArrayList<>();
        File[] list = new File(ROOT, "Themes").listFiles();
        if (list != null) {
            for (File f : list) {
                themes.add(new Theme(f.getName()));
            }
        }
        return themes;
    }

    private List<Language> cached;
    @Override
    public Collection<? extends Language> getAvailableLanguages() {
        if (cached != null) return cached;
        List<Language> languages = new ArrayList<>();
        File[] list = new File(ROOT, "Locales").listFiles();
        if (list != null) {
            for (File f : list) {
                Language l = new Language(f.getName().replace(".yml", ""));
                try (FileReader reader = new FileReader(f)) {
                    l.loadLanguage(reader);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return cached = languages;
    }
}
