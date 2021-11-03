package thito.nodeflow.plugin.base.blueprint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import thito.nodeflow.internal.project.Project;
import thito.nodeflow.internal.project.module.FileModule;
import thito.nodeflow.internal.project.module.FileStructure;
import thito.nodeflow.internal.project.module.FileViewer;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.plugin.base.blueprint.element.BlueprintClass;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class BlueprintViewer implements FileViewer {
    private Project project;
    private Resource resource;
    private FileModule module;
    private BorderPane node = new BorderPane();
    private BlueprintStructure structure = new BlueprintStructure();
    private ObjectProperty<BlueprintStructure.AbstractItem> focusedItem = new SimpleObjectProperty<>();

    private BlueprintClass blueprintClass;

    public BlueprintViewer(Project project, Resource resource, FileModule module) {
        this.project = project;
        this.resource = resource;
        this.module = module;
    }

    @Override
    public FileStructure getStructure() {
        return structure;
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
        return null;
    }

    @Override
    public void reload(byte[] data) {
        if (data != null && data.length > 0) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
                blueprintClass = new BlueprintClass();

            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
            return;
        }
        blueprintClass = new BlueprintClass();
    }
}
