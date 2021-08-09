package thito.nodeflow.api.node.eventbus;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.ui.*;

public interface EventProviderCategory extends NodeProviderCategory {
    static EventProviderCategory create(ProjectFacet facet, String alias, String name, Icon icon) {
        return NodeFlow.getApplication().getModuleManager().createEventProviderCategory(facet, alias, name, icon);
    }

    ProjectFacet getFacet();

    EventProvider addProvider(String id, String name);

    void unregister();
}
