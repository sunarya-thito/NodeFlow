package thito.nodeflow.internal.node.eventbus;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.node.*;

import java.util.*;

public class EventProviderCategoryImpl implements EventProviderCategory {
    private String name;
    private String alias;
    private Icon icon;
    private List<NodeProvider> providers = new ArrayList<>();
    private ProjectFacet facet;

    public EventProviderCategoryImpl(ProjectFacet facet, String name, String alias, Icon icon) {
        this.facet = facet;
        this.name = name;
        this.alias = alias;
        this.icon = icon;
    }

    @Override
    public ProjectFacet getFacet() {
        return facet;
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

    @Override
    public EventProvider addProvider(String id, String name) {
        EventProvider provider = new EventProviderImpl(id, name, this);
        providers.add(provider);
        return provider;
    }

    @Override
    public void unregister() {
        ModuleManagerImpl.getInstance().getCategories().remove(this);
    }
}
