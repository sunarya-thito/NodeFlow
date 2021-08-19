package thito.nodeflow.internal.project.module;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.library.resource.*;

public interface FileViewer {
    Resource getResource();
    FileModule getModule();
    Node getNode();
    ObservableList<Menu> getAdditionalMenus();
}
