package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public interface ModuleManager {
    void loadModule(NodeModule module, InputStream inputStream, boolean ignoreMissingProvider) throws MissingProviderException;
    void loadHeadlessModule(NodeModule module, InputStream inputStream);
    void saveModule(NodeModule module, OutputStream outputStream);
    NodeProvider getProvider(String id);
    void registerCategory(NodeProviderCategory category);
    ParameterEditor getEditorForContentType(Class<?> type);
    EventProviderCategory createEventProviderCategory(ProjectFacet facet, String alias, String name, Icon icon);
}
