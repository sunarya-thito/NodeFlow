package thito.nodeflow.api;

import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.event.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

import java.util.logging.*;

public abstract class NodeFlow {
    private static NodeFlow instance;

    protected NodeFlow() {
        if (instance != null) throw new IllegalStateException("Already initialized");
        instance = this;
    }

    public static NodeFlow getApplication() {
        return instance;
    }

    public static Logger getMainLogger() {
        return getApplication().getLogger();
    }

    public abstract CommandManager getCommandManager();

    public abstract Logger getLogger();

    public abstract Version getVersion();

    public abstract Updater getUpdater();

    public abstract EventManager getEventManager();

    public abstract BundleManager getBundleManager();

    public abstract ApplicationSettings getSettings();

    public abstract ResourceManager getResourceManager();

    public abstract LocaleManager getLocaleManager();

    public abstract ModuleManager getModuleManager();

    public abstract ProjectManager getProjectManager();

    public abstract UIManager getUIManager();

    public abstract TaskManager getTaskManager();

    public abstract EditorManager getEditorManager();

    public abstract Toolkit getToolkit();

    public abstract Section getConfiguration(ProjectFacet facet);

    public abstract void shutdown();
}
