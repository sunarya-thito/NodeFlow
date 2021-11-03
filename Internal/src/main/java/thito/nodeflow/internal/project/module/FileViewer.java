package thito.nodeflow.internal.project.module;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.resource.*;

public interface FileViewer {
    Project getProject();
    Resource getResource();
    FileModule getModule();
    Node getNode();
    ObservableList<Menu> getAdditionalMenus();
    void reload(byte[] data);
    FileStructure getStructure();
}
