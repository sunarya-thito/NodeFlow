package thito.nodeflow.internal.project.module;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.internal.resource.*;

public class UnknownFileViewer implements FileViewer {
    private Project project;
    private UnknownFileModule module;
    private Resource resource;
    private UnknownTabSkin unknownTabSkin;
    private ObservableList<Menu> menus = FXCollections.observableArrayList();

    public UnknownFileViewer(UnknownFileModule module, Project project, Resource resource) {
        this.project = project;
        this.module = module;
        this.resource = resource;
        unknownTabSkin = new UnknownTabSkin();
    }

    @Override
    public void reload(byte[] data) {
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public FileModule getModule() {
        return module;
    }

    @Override
    public Node getNode() {
        return unknownTabSkin;
    }

    @Override
    public ObservableList<Menu> getAdditionalMenus() {
        return menus;
    }
}
