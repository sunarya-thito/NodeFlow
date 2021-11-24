package thito.nodeflow.project.module;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.project.Project;
import thito.nodeflow.resource.Resource;

public interface FileViewer {
    Project getProject();
    Resource getResource();
    FileModule getModule();
    Node getNode();
    ObservableList<Menu> getAdditionalMenus();
    void reload(byte[] data);
    FileStructure getStructure();
}
