package thito.nodeflow.internal;

import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.util.logging.*;

public class NodeFlow {

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    public static void launch() {
        logger = Logger.getLogger("NodeFlow");
        new TaskManager(); // initializes it
        new ThemeManager();
        ResourceWatcher.getResourceWatcher().open();

        // for debugging purpose
        ThemeManager.getInstance().setTheme(new Theme(
                new File("C:\\Users\\Thito\\IdeaProjects\\NodeFlow Software\\Internal\\src\\main\\resources\\Theme\\Dark")
        ));
        //

        new DashboardWindow().show();
    }

    public static void shutdown() {
        ResourceWatcher.getResourceWatcher().close();
        TaskManager.getInstance().shutdown();
    }

}
