package thito.nodeflow.internal;

import thito.nodeflow.internal.task.*;

import java.util.logging.*;

public class NodeFlow {

    private static Logger logger;
    private static TaskManager taskManager;

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void launch() {
        logger = Logger.getLogger("NodeFlow");
        taskManager = new TaskManager();
    }

}
