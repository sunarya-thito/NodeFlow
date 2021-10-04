package thito.nodeflow.internal.plugin;

public abstract class ProjectHandler {
    private final Plugin plugin;
    private final ProjectHandlerRegistry registry;

    public ProjectHandler(Plugin plugin, ProjectHandlerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ProjectHandlerRegistry getRegistry() {
        return registry;
    }
}
