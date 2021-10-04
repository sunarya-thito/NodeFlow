package thito.nodeflow.website;

import thito.nodeflow.internal.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.website.search.*;

import java.io.*;
import java.util.logging.*;

public class WebsitePlugin implements PluginInstance {

    private URLSearchProvider provider = new URLSearchProvider();

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        SearchManager.getInstance().getProviderList().add(provider);
        try {
            PluginManager.getPluginManager().loadPluginLocale(NodeFlow.getInstance().getLanguage("en_us"), getPlugin(), getPlugin().getClassLoader().getResourceAsStream("en_us.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to read plugin language", e);
        }
    }

    @Override
    public void onDisable() {
        SearchManager.getInstance().getProviderList().remove(provider);
    }
}
