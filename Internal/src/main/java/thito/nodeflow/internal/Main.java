package thito.nodeflow.internal;

import com.sun.javafx.css.*;
import javafx.application.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.stage.*;
import thito.nodeflow.internal.protocol.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.general.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Main extends Application {
    static {
        AdvancedPseudoClass.init();
    }
    @Override
    public void start(Stage stage) throws Exception {
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

        splashScreen.progressProperty().bind(totalProgress);
        splashScreen.statusProperty().bind(status);

        TaskThread.IO().schedule(() -> {
            ThemeManager.init();
            nodeFlow.getSettingsManager().addCategories(General.class);

            ResourceWatcher.getResourceWatcher().open();

            nodeFlow.submitProgressedTask((progress, stat) -> {
                stat.set("Loading settings configuration");
                File target = new File(NodeFlow.ROOT, "nodeflow.yml");
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

                    if (Settings.get(General.Appearance.class).getShowDashboardAtStart().get()) {
                        dashboardWindow.show();
                    }

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
}
