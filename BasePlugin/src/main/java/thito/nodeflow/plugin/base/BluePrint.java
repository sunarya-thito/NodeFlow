package thito.nodeflow.plugin.base;

import thito.nodeflow.engine.node.LinkStyle;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.plugin.Plugin;
import thito.nodeflow.plugin.PluginInstance;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.settings.SettingsManager;
import thito.nodeflow.plugin.base.blueprint.BlueprintModule;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;

import java.util.logging.Level;

public class BluePrint implements PluginInstance {

    @Override
    public Batch.Task createLoaderTask() {
        PluginManager manager = getManager();
        Plugin plugin = getPlugin();
        return Batch.execute(TaskThread.IO(), progress -> {
            progress.setStatus("Loading plugin locale");
            try {
                manager.loadPluginLocale(NodeFlow.getInstance().getLanguage("en_us"), plugin,
                        plugin.getClassLoader().getResourceAsStream("en_us.yml"));
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to load plugin locale ", t);
            }
        }).execute(TaskThread.BG(), progress -> {
            progress.setStatus("Registering settings objects");
            SettingsManager.getSettingsManager().registerParser(LinkStyle.class, new LinkStyleParser());
        });
    }

    @Override
    public Batch.Task createInitializationTask() {
        return Batch.execute(TaskThread.BG(), progress -> {
            progress.setStatus("Registering Blueprint Module");
            PluginManager pluginManager = PluginManager.getPluginManager();
            pluginManager.registerFileModule(new BlueprintModule());
        });
    }

    @Override
    public Batch.Task createShutdownTask() {
        return Batch.execute(TaskThread.BG(), progress -> {
            progress.setStatus("Unregistering Blueprint Module");
            PluginManager.getPluginManager().unregisterFileModule(getPlugin());
        });
    }

}
