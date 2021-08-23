package thito.nodeflow.internal.project.module;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.library.resource.*;

import java.nio.charset.*;

public class UnknownFileViewer implements FileViewer {
    private Project project;
    private UnknownFileModule module;
    private Resource resource;
    private TextArea node;
    private ObservableList<Menu> menus = FXCollections.observableArrayList();

    public UnknownFileViewer(UnknownFileModule module, Project project, Resource resource, byte[] data) {
        this.project = project;
        this.module = module;
        this.resource = resource;
        node = new TextArea();
        node.setEditable(false);
        node.setText(new String(data, StandardCharsets.UTF_8));
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
        return node;
    }

    @Override
    public ObservableList<Menu> getAdditionalMenus() {
        return menus;
    }
}
