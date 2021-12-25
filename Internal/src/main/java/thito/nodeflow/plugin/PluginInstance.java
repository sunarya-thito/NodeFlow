package thito.nodeflow.plugin;

import thito.nodeflow.task.batch.Batch;

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
    Batch.Task createLoaderTask();
    Batch.Task createInitializationTask();
    Batch.Task createShutdownTask();
}
