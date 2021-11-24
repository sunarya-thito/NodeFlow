package thito.nodeflow.plugin.base;

import thito.nodeflow.engine.node.LinkStyle;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.plugin.Plugin;
import thito.nodeflow.plugin.PluginInstance;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.settings.SettingsManager;
import thito.nodeflow.task.BatchTask;
import thito.nodeflow.task.ProgressedTask;
import thito.nodeflow.plugin.base.blueprint.BlueprintModule;

import java.util.logging.Level;

public class BluePrint implements PluginInstance {

    @Override
    public BatchTask createLoaderTask() {
        BatchTask batchTask = new BatchTask();
        PluginManager manager = getManager();
        Plugin plugin = getPlugin();
        batchTask.submitTask(progress -> {
            progress.setStatus("Loading plugin locale");
            try {
                manager.loadPluginLocale(NodeFlow.getInstance().getLanguage("en_us"), plugin,
                        plugin.getClassLoader().getResourceAsStream("en_us.yml"));
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to load plugin locale ", t);
            }
        });
        batchTask.submitTask(progress -> {
            progress.setStatus("Registering settings objects");
            SettingsManager.getSettingsManager().registerParser(LinkStyle.class, new LinkStyleParser());
        });
        return batchTask;
    }

    @Override
    public BatchTask createInitializationTask() {
        BatchTask task = new BatchTask();
        task.submitTask(progress -> {
            progress.setStatus("Registering Blueprint Module");
            PluginManager pluginManager = PluginManager.getPluginManager();
            pluginManager.registerFileModule(new BlueprintModule());
        });
        return task;
    }

    @Override
    public BatchTask createShutdownTask() {
        BatchTask task = new BatchTask();
        task.submitTask(progress -> {
            progress.setStatus("Unregistering Blueprint Module");
            PluginManager.getPluginManager().unregisterFileModule(getPlugin());
        });
        return task;
    }

}
