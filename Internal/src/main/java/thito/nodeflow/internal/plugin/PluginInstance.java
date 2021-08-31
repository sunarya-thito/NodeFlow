package thito.nodeflow.internal.plugin;

import java.util.logging.*;

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
    void onLoad();
    void onEnable();
    void onDisable();
}
