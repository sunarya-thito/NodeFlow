package thito.nodeflow.internal;

import javafx.application.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.protocol.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.general.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.application.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeFlow extends ApplicationResources {

    public static final File ROOT;

    static {
        String rootProp = System.getProperty("nodeflow.rootDirectory", "");
        ROOT = new File(rootProp).getAbsoluteFile();
    }

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    public static void launch() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler());
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("sun.java3d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "false");

        logger = Logger.getLogger("NodeFlow");
        logger.log(Level.INFO, "Loading application...");

        NodeFlow nodeFlow = new NodeFlow();
        nodeFlow.registerProtocol("rsrc", new ResourceProtocol());
        nodeFlow.registerProtocol("plugin", new PluginResourceProtocol());

        TaskManager.init();

        try (InputStreamReader reader = new InputStreamReader(new URL("rsrc:ChangeLogs.txt").openStream())) {
            Version.read(reader);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show();

        Platform.setImplicitExit(true);

        StringProperty status = new SimpleStringProperty();
        DoubleProperty totalProgress = new SimpleDoubleProperty();

        splashScreen.progressProperty().bind(totalProgress);
        splashScreen.statusProperty().bind(status);

        TaskThread.IO().schedule(() -> {
            ThemeManager.init();
            nodeFlow.getSettingsManager().addCategories(General.class);

            ResourceWatcher.getResourceWatcher().open();

            nodeFlow.submitProgressedTask((progress, stat) -> {
                stat.set("Loading settings configuration");
                File target = new File(ROOT, "nodeflow.yml");
                if (target.exists()) {
                    try (FileReader reader = new FileReader(target)) {
                        nodeFlow.getSettingsManager().load(reader);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to load nodeflow.yml", e);
                    }
                }
            });

            runLoaderTask(nodeFlow.progressedTasks, 0, status, totalProgress, () -> {
                Platform.runLater(() -> {
                    logger.log(Level.INFO, "Starting Application...");
                    Thread.setDefaultUncaughtExceptionHandler(new ReportedExceptionHandler());
                    DashboardWindow dashboardWindow = new DashboardWindow();
                    EditorWindow editorWindow = new Editor().getEditorWindow();
                    editorWindow.show();
                    dashboardWindow.show();

                    splashScreen.close();
                });
            });
        });

    }

    private static void runLoaderTask(Queue<ProgressedTask> taskList, int progressDone, StringProperty status, DoubleProperty totalProgress, Runnable onDone) {
        ProgressedTask finalTask = taskList.poll();
        if (finalTask != null) {
            onDone.run();
            return;
        }
        Platform.runLater(() -> {
            int remaining = taskList.size();
            DoubleProperty currentProgress = new SimpleDoubleProperty();
            totalProgress.bind(Bindings.createDoubleBinding(() -> {
                int total = remaining + progressDone;
                return (progressDone + currentProgress.get()) / (total + 1d);
            }, currentProgress));
            TaskThread.IO().schedule(() -> {
                finalTask.run(currentProgress, status);
                runLoaderTask(taskList, progressDone + 1, status, totalProgress, onDone);
            });
        });
    }

    public static NodeFlow getInstance() {
        return (NodeFlow) ApplicationResources.getInstance();
    }

    public static void shutdown() {
        ResourceWatcher.getResourceWatcher().close();
        TaskManager.getInstance().shutdown();
    }

    private Language defaultLanguage = new Language("en_us");
    private SettingsManager settingsManager = new SettingsManager();
    private LinkedList<ProgressedTask> progressedTasks = new LinkedList<>();
    private ObjectProperty<Workspace> workspace = new SimpleObjectProperty<>();

    public NodeFlow() {
        try (FileReader reader = new FileReader(new File(ROOT, "Locales/en_us.yml"))) {
            defaultLanguage.loadLanguage(reader);
            Language.setLanguage(defaultLanguage);
        } catch (Throwable t) {
            throw new RuntimeException("failed to load default language (en_us.yml)");
        }
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

    @Override
    public Collection<? extends Language> getAvailableLanguages() {
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
        return languages;
    }
}
