package thito.nodeflow.plugin;

import thito.nodeflow.task.BatchTask;

import java.util.logging.Logger;

public interface PluginInstance {
    default PluginManager getManager() {
        return PluginManager.getPluginManager();
    }
    default Plugin getPlugin() {
        return Plugin.getPlugin(getClass());
    }
    default Logger getLogger() {
        return getPlugin().getLogger();
    }
    BatchTask createLoaderTask();
    BatchTask createInitializationTask();
    BatchTask createShutdownTask();
}
