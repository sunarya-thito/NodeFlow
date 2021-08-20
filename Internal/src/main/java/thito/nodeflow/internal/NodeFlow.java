package thito.nodeflow.internal;

import javafx.application.*;
import thito.nodeflow.internal.protocol.*;
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

    public static final File ROOT
//            = new File(""); // FOR PRODUCTION
            = new File("src/main/resources"); //  FOR DEBUGGING PURPOSES ONLY

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    public static void launch() {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("sun.java3d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "false");

        logger = Logger.getLogger("NodeFlow");
        logger.log(Level.INFO, "Loading application...");

        NodeFlow nodeFlow = new NodeFlow();
        nodeFlow.registerProtocol("rsrc", new ResourceProtocol());
        nodeFlow.registerProtocol("plugin", new PluginResourceProtocol());

        new TaskManager(); // initializes the task manager

        try (InputStreamReader reader = new InputStreamReader(new URL("rsrc:ChangeLogs.txt").openStream())) {
            Version.read(reader);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show();

        TaskThread.IO().schedule(() -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

            ResourceWatcher.getResourceWatcher().open();

            new ThemeManager();
            // for debugging purpose
            ThemeManager.getInstance().setTheme(new Theme("Dark"));
            //

            Language lang = new Language("en_us");
            try (FileReader reader = new FileReader(new File(ROOT, "Locales/en_us.yml"))) {
                lang.loadLanguage(reader);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            Language.setLanguage(lang);

            Platform.runLater(() -> {
                DashboardWindow dashboardWindow = new DashboardWindow();
                EditorWindow editorWindow = new EditorWindow();
                dashboardWindow.getStage().initOwner(editorWindow.getStage());
                editorWindow.show();
                dashboardWindow.show();

                splashScreen.close();
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
