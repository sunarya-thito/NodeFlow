package thito.nodeflow.plugin.base;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.plugin.base.module.*;

import java.io.*;
import java.util.logging.*;

public class BluePrint implements PluginInstance {

    @Override
    public void onLoad() {
        try {
            PluginManager manager = getManager();
            Plugin plugin = getPlugin();
            manager.loadPluginLocale(NodeFlow.getInstance().getLanguage("en_us"), plugin,
                    plugin.getClassLoader().getResourceAsStream("en_us.yml"));
            SettingsManager.getSettingsManager().registerParser(LinkStyle.class, new LinkStyleParser());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to load plugin locale en_us", e);
        }
    }

    @Override
    public void onEnable() {
        PluginManager.getPluginManager().registerFileModule(new BlueprintModule());
    }

    @Override
    public void onDisable() {
        PluginManager.getPluginManager().unregisterFileModule(getPlugin());
    }

}
