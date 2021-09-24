package thito.nodeflow.website;

import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.website.search.*;

public class WebsitePlugin implements PluginInstance {

    private URLSearchProvider provider = new URLSearchProvider();

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        SearchManager.getInstance().getProviderList().add(provider);
    }

    @Override
    public void onDisable() {
        SearchManager.getInstance().getProviderList().remove(provider);
    }
}
