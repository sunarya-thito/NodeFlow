package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.ui.*;

import java.util.*;

public class SimpleNodeProviderCategory implements NodeProviderCategory {
    private String name;
    private String alias;
    private List<NodeProvider> providers = new ArrayList<>();
    private Icon icon;

    public SimpleNodeProviderCategory(String name, String alias, Icon icon) {
        this.name = name;
        this.alias = alias;
        this.icon = icon;
    }

    public NodeProvider findProvider(String id) {
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).getID().equals(id)) {
                return providers.get(i);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public List<NodeProvider> getProviders() {
        return providers;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }
}
