package thito.nodeflow.internal;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.stage.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.protocol.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.application.*;
import thito.nodeflow.internal.task.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dashboard.*;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class Main extends Application {
    static {
        AdvancedPseudoClass.init();
    }
    @Override
    public void start(Stage stage) throws Exception {
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

        NodeFlow nodeFlow = new NodeFlow();
        nodeFlow.registerProtocol("rsrc", new ResourceProtocol());
        nodeFlow.registerProtocol("plugin", new PluginResourceProtocol());
        nodeFlow.registerProtocol("theme", new ThemeProtocol());

        TaskManager.init();

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

        ThreadBinding.bind(splashScreen.statusProperty(), status, TaskThread.UI());
        ThreadBinding.bind(splashScreen.progressProperty(), totalProgress, TaskThread.UI());

        TaskThread.IO().schedule(() -> {
            BatchTask batchTask = new BatchTask();
            ThemeManager.init();

            ResourceWatcher.getResourceWatcher().open();

            SettingsManager settingsManager = SettingsManager.getSettingsManager();

            settingsManager.registerCanvas(General.class);
            settingsManager.registerCanvas(Appearance.class);

            batchTask.submitTask(new ProgressedTask("Loading setting configuration", progress -> {
                Settings.getSettings().loadGlobalConfiguration();
            }));

            nodeFlow.createInitializationTasks(batchTask);

            batchTask.submitTask(new ProgressedTask("Finalizing things up", progress -> {
                Thread.setDefaultUncaughtExceptionHandler(new ReportedExceptionHandler());
                TaskThread.UI().schedule(() -> {
                    DashboardWindow dashboardWindow = new DashboardWindow();
                    if (Settings.getSettings().getCategory(Appearance.class).showDashboardAtStart.get()) {
                        dashboardWindow.show();
                    }
                    splashScreen.close();
                });
            }));

            logger.log(Level.INFO, "Starting application...");
            batchTask.start(TaskThread.BG(), status, totalProgress);
        });
    }

}
