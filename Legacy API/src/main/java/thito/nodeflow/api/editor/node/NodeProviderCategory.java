package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.ui.*;

import java.util.*;

public interface NodeProviderCategory {
    String getName();
    String getAlias();
    List<NodeProvider> getProviders();
    Icon getIcon();
    default NodeProvider findProvider(String id) {
        for (int i = 0; i < getProviders().size(); i++) {
            NodeProvider provider = getProviders().get(i);
            if (provider.getID().equals(id)) {
                return provider;
            }
        }
        return null;
    }
}
