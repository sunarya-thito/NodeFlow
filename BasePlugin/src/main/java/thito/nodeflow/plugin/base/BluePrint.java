package thito.nodeflow.plugin.base;

import thito.nodeflow.engine.node.LinkStyle;
import thito.nodeflow.internal.NodeFlow;
import thito.nodeflow.internal.plugin.Plugin;
import thito.nodeflow.internal.plugin.PluginInstance;
import thito.nodeflow.internal.plugin.PluginManager;
import thito.nodeflow.internal.settings.SettingsManager;
import thito.nodeflow.internal.task.BatchTask;
import thito.nodeflow.internal.task.ProgressedTask;
import thito.nodeflow.plugin.base.blueprint.BlueprintModule;

import java.util.logging.Level;

public class BluePrint implements PluginInstance {

    @Override
    public BatchTask createLoaderTask() {
        BatchTask batchTask = new BatchTask();
        PluginManager manager = getManager();
        Plugin plugin = getPlugin();
        batchTask.submitTask(new ProgressedTask("Loading plugin locale", progress -> {
            try {
                manager.loadPluginLocale(NodeFlow.getInstance().getLanguage("en_us"), plugin,
                        plugin.getClassLoader().getResourceAsStream("en_us.yml"));
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to load plugin locale ", t);
            }
        }));
        batchTask.submitTask(new ProgressedTask("Registering settings", progress -> {
            SettingsManager.getSettingsManager().registerParser(LinkStyle.class, new LinkStyleParser());
        }));
        return batchTask;
    }

    @Override
    public BatchTask createInitializationTask() {
        BatchTask task = new BatchTask();
        task.submitTask(new ProgressedTask("Registering Blueprint Module", progress -> {
            PluginManager pluginManager = PluginManager.getPluginManager();
            pluginManager.registerFileModule(new BlueprintModule());
        }));
        return task;
    }

    @Override
    public BatchTask createShutdownTask() {
        BatchTask task = new BatchTask();
        task.submitTask(new ProgressedTask("Unregistering Blueprint Module", progress -> {
            PluginManager.getPluginManager().unregisterFileModule(getPlugin());
        }));
        return task;
    }

}
