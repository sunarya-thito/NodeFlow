package thito.nodeflow.project.module;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.project.Project;
import thito.nodeflow.resource.Resource;

public interface FileViewer {
    Project getProject();
    Resource getResource();
    FileModule getModule();
    Node getNode();
    ObservableList<Menu> getAdditionalMenus();
    @IOThread
    void reload(byte[] data);
    FileStructure getStructure();
    void close();
}
