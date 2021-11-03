package thito.nodeflow.internal.plugin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.internal.ui.FormPanel;

public abstract class ProjectHandler {
    private final Plugin plugin;
    private final ProjectHandlerRegistry registry;
    private final ObservableList<FormPanel> formPanels = FXCollections.observableArrayList();

    public ProjectHandler(Plugin plugin, ProjectHandlerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ProjectHandlerRegistry getRegistry() {
        return registry;
    }

    public ObservableList<FormPanel> getFormPanels() {
        return formPanels;
    }
}
