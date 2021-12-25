package thito.nodeflow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.config.Section;
import thito.nodeflow.language.Language;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.protocol.PluginResourceProtocol;
import thito.nodeflow.protocol.ResourceProtocol;
import thito.nodeflow.protocol.ThemeProtocol;
import thito.nodeflow.resource.ResourceWatcher;
import thito.nodeflow.settings.Settings;
import thito.nodeflow.settings.SettingsManager;
import thito.nodeflow.settings.application.Appearance;
import thito.nodeflow.settings.application.General;
import thito.nodeflow.task.TaskManager;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.task.batch.Progress;
import thito.nodeflow.ui.AdvancedPseudoClass;
import thito.nodeflow.ui.SplashScreen;
import thito.nodeflow.ui.ThemeManager;
import thito.nodeflow.ui.dashboard.DashboardWindow;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    static {
        AdvancedPseudoClass.init();
    }
    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        Thread.currentThread().setName("UI");
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler());
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("sun.java3d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "false");

        Logger logger = Logger.getLogger("NodeFlow");

        PrintStream oldErr = System.err;
        new File(NodeFlow.ROOT, "Logs").mkdirs();
        try {
            LoggerInjector.init();
        } catch (IOException e) {
            e.printStackTrace(oldErr);
            System.exit(1);
        }

        logger.log(Level.INFO, "Loading application...");
        logger.log(Level.INFO, "Root directory at "+NodeFlow.ROOT);
        logger.log(Level.INFO, "Resources Root directory at "+NodeFlow.RESOURCES_ROOT);
        TaskManager.init();
        NodeFlow nodeFlow = new NodeFlow();
        nodeFlow.registerProtocol("rsrc", new ResourceProtocol());
        nodeFlow.registerProtocol("plugin", new PluginResourceProtocol());
        nodeFlow.registerProtocol("theme", new ThemeProtocol());

        try (InputStreamReader reader = new InputStreamReader(new URL("rsrc:ChangeLogs.txt").openStream())) {
            Version.read(reader);
            String deployVersion = System.getProperty("nodeflow.version", "");
            if (deployVersion.equals("${env.NODEFLOW_VERSION}")) deployVersion = "B";
            if (!deployVersion.isEmpty()) {
                Version.getCurrentVersion().setVersion(Version.getCurrentVersion().getVersion() + "-" + deployVersion);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show();

        StringProperty status = new SimpleStringProperty();
        DoubleProperty totalProgress = new SimpleDoubleProperty();

        ThreadBinding.bind(splashScreen.statusProperty(), status, TaskThread.BG(), TaskThread.UI());
        ThreadBinding.bind(splashScreen.progressProperty(), totalProgress, TaskThread.BG(), TaskThread.UI());

        Batch
            .execute(TaskThread.BG(), progress -> {
                progress.setStatus("Initializing theme manager");
                ThemeManager.init();
            }).execute(TaskThread.IO(), progress -> {
                progress.setStatus("Initializing resource watcher");
                ResourceWatcher.getResourceWatcher().open();
            }).execute(TaskThread.IO(), progress -> {
                progress.setStatus("Loading languages");
                nodeFlow.getAvailableLanguages();
                nodeFlow.setDefaultLanguage(nodeFlow.getLanguage("en_us"));
            }).execute(TaskThread.BG(), progress -> {
                progress.setStatus("Setting language");
                Language.setLanguage(nodeFlow.getDefaultLanguage());
            }).execute(TaskThread.IO(), progress -> {
                progress.setStatus("Loading settings");
                Section section = NodeFlow.getInstance().readFromConfiguration();
                progress.insert(TaskThread.BG(), p -> {
                    SettingsManager settingsManager = SettingsManager.getSettingsManager();
                    progress.setStatus("Reading settings");
                    settingsManager.registerCanvas(General.class);
                    settingsManager.registerCanvas(Appearance.class);
                    Settings.getSettings().loadGlobalConfiguration(section);
                });
            }).execute(nodeFlow.createInitializationTasks())
            .execute(TaskThread.BG(), progress -> {
                progress.setStatus("Initializing crash handler");
                Thread.setDefaultUncaughtExceptionHandler(new ReportedExceptionHandler());
            })
            .execute(TaskThread.UI(), progress -> {
                progress.setStatus("Launching dashboard");
                DashboardWindow dashboardWindow = new DashboardWindow();
                if (Settings.getSettings().getCategory(Appearance.class).showDashboardAtStart.get()) {
                    dashboardWindow.show();
                }
                splashScreen.close();
            })
            .start(new Progress(status, totalProgress));
        logger.log(Level.INFO, "Starting application...");
    }

}
