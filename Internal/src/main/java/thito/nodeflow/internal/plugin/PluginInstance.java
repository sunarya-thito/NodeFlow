package thito.nodeflow.internal.plugin;

public interface PluginInstance {
    default Plugin getPlugin() {
        return Plugin.getPlugin(getClass());
    }
}
